package com.dovar.router_api.eventbus;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.dovar.router_api.Debugger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 事件总线
 * note:
 * 要求：事件总线的key = 组件key + event名.
 * <p>
 * 为了防止事件滥用，在发送事件时需要检查事件类型是否为已注册的类型，如果不是则不允许发送。
 */
public class LiveEventBus {

    private final Map<String, BusMutableLiveData<Object>> bus;

    private LiveEventBus() {
        bus = new HashMap<>();
    }

    private static class SingletonHolder {
        private static final LiveEventBus DEFAULT_BUS = new LiveEventBus();
    }

    public static LiveEventBus instance() {
        return SingletonHolder.DEFAULT_BUS;
    }

    synchronized Observable with(String key) {
        if (!bus.containsKey(key)) {
            bus.put(key, new BusMutableLiveData<>(key));
        }
        return bus.get(key);
    }

    public void subscribe(String key, LifecycleOwner owner, Observer observer) {
        if (TextUtils.isEmpty(key) || owner == null || observer == null) return;
        with(key).observe(owner, observer);
    }

    public void unsubscribe(String key, Observer observer) {
        if (TextUtils.isEmpty(key) || observer == null) return;
        if (bus.containsKey(key)) {
            with(key).removeObserver(observer);
        }
    }

    public void publish(String key, Object obj) {
        if (TextUtils.isEmpty(key)) return;
        //为了防止事件滥用，在发送事件时需要检查事件类型是否为已注册的类型，如果不是则不允许发送。
        if (bus.containsKey(key)) {
            with(key).postValue(obj);
        }
    }

    public interface Observable<T> {

        void setValue(T value);

        void postValue(T value);

        void postValueDelay(T value, long delay, TimeUnit unit);

        void observe(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer);

        void observeSticky(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer);

        void observeForever(@NonNull Observer<T> observer);

        void observeStickyForever(@NonNull Observer<T> observer);

        void removeObserver(@NonNull Observer<T> observer);
    }

    private static class BusMutableLiveData<T> extends MutableLiveData<T> implements Observable<T> {

        private class PostValueTask implements Runnable {
            private Object newValue;

            public PostValueTask(@NonNull Object newValue) {
                this.newValue = newValue;
            }

            @Override
            public void run() {
                setValue((T) newValue);
            }
        }

        @NonNull
        private final String key;
        private Map<Observer, Observer> observerMap = new HashMap<>();
        private Handler mainHandler = new Handler(Looper.getMainLooper());


        private BusMutableLiveData(String key) {
            this.key = key;
        }

        @Override
        public void postValue(T value) {
            mainHandler.post(new PostValueTask(value));
        }

        @Override
        public void postValueDelay(T value, long delay, TimeUnit unit) {
            mainHandler.postDelayed(new PostValueTask(value), unit.convert(delay, unit));
        }

        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
            //保存LifecycleOwner的当前状态
            Lifecycle lifecycle = owner.getLifecycle();
            Lifecycle.State currentState = lifecycle.getCurrentState();
            int observerSize = getLifecycleObserverMapSize(lifecycle);
            boolean needChangeState = currentState.isAtLeast(Lifecycle.State.STARTED);
            if (needChangeState) {
                //把LifecycleOwner的状态改为INITIALIZED
                setLifecycleState(lifecycle, Lifecycle.State.INITIALIZED);
                //set observerSize to -1，否则super.observe(owner, observer)的时候会无限循环
                setLifecycleObserverMapSize(lifecycle, -1);
            }
            super.observe(owner, observer);
            if (needChangeState) {
                //重置LifecycleOwner的状态
                setLifecycleState(lifecycle, currentState);
                //重置observer size，因为又添加了一个observer，所以数量+1
                setLifecycleObserverMapSize(lifecycle, observerSize + 1);
                //把Observer置为active
                hookObserverActive(observer, true);
            }
            //更改Observer的version
            hookObserverVersion(observer);
        }

        public void observeSticky(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
            super.observe(owner, observer);
        }

        @Override
        public void observeForever(@NonNull Observer<T> observer) {
            if (!observerMap.containsKey(observer)) {
                observerMap.put(observer, new ObserverWrapper(observer));
            }
            super.observeForever(observerMap.get(observer));
        }

        public void observeStickyForever(@NonNull Observer<T> observer) {
            super.observeForever(observer);
        }

        @Override
        public void removeObserver(@NonNull Observer<T> observer) {
            Observer realObserver = null;
            if (observerMap.containsKey(observer)) {
                realObserver = observerMap.remove(observer);
            } else {
                realObserver = observer;
            }
            super.removeObserver(realObserver);
            if (!hasObservers()) {
                LiveEventBus.instance().bus.remove(key);
            }
        }

        private void setLifecycleObserverMapSize(Lifecycle lifecycle, int size) {
            if (lifecycle == null) {
                return;
            }
            if (!(lifecycle instanceof LifecycleRegistry)) {
                return;
            }
            try {
                Field observerMapField = LifecycleRegistry.class.getDeclaredField("mObserverMap");
                observerMapField.setAccessible(true);
                Object mObserverMap = observerMapField.get(lifecycle);
                Class<?> superclass = mObserverMap.getClass().getSuperclass();
                Field mSizeField = superclass.getDeclaredField("mSize");
                mSizeField.setAccessible(true);
                mSizeField.set(mObserverMap, size);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private int getLifecycleObserverMapSize(Lifecycle lifecycle) {
            if (lifecycle == null) {
                return 0;
            }
            if (!(lifecycle instanceof LifecycleRegistry)) {
                return 0;
            }
            try {
                Field observerMapField = LifecycleRegistry.class.getDeclaredField("mObserverMap");
                observerMapField.setAccessible(true);
                Object mObserverMap = observerMapField.get(lifecycle);
                Class<?> superclass = mObserverMap.getClass().getSuperclass();
                Field mSizeField = superclass.getDeclaredField("mSize");
                mSizeField.setAccessible(true);
                return (int) mSizeField.get(mObserverMap);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }

        private void setLifecycleState(Lifecycle lifecycle, Lifecycle.State state) {
            if (lifecycle == null) {
                return;
            }
            if (!(lifecycle instanceof LifecycleRegistry)) {
                return;
            }
            try {
                Field mState = LifecycleRegistry.class.getDeclaredField("mState");
                mState.setAccessible(true);
                mState.set(lifecycle, state);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private Object getObserverWrapper(@NonNull Observer<T> observer) throws Exception {
            Field fieldObservers = LiveData.class.getDeclaredField("mObservers");
            fieldObservers.setAccessible(true);
            Object objectObservers = fieldObservers.get(this);
            Class<?> classObservers = objectObservers.getClass();
            Method methodGet = classObservers.getDeclaredMethod("get", Object.class);
            methodGet.setAccessible(true);
            Object objectWrapperEntry = methodGet.invoke(objectObservers, observer);
            Object objectWrapper = null;
            if (objectWrapperEntry instanceof Map.Entry) {
                objectWrapper = ((Map.Entry) objectWrapperEntry).getValue();
            }
            return objectWrapper;
        }

        private void hookObserverVersion(@NonNull Observer<T> observer) {
            try {
                //get wrapper's version
                Object objectWrapper = getObserverWrapper(observer);
                if (objectWrapper == null) {
                    return;
                }
                Class<?> classObserverWrapper = objectWrapper.getClass().getSuperclass();
                Field fieldLastVersion = classObserverWrapper.getDeclaredField("mLastVersion");
                fieldLastVersion.setAccessible(true);
                //get livedata's version
                Field fieldVersion = LiveData.class.getDeclaredField("mVersion");
                fieldVersion.setAccessible(true);
                Object objectVersion = fieldVersion.get(this);
                //set wrapper's version
                fieldLastVersion.set(objectWrapper, objectVersion);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void hookObserverActive(@NonNull Observer<T> observer, boolean active) {
            try {
                //get wrapper's version
                Object objectWrapper = getObserverWrapper(observer);
                if (objectWrapper == null) {
                    return;
                }
                Class<?> classObserverWrapper = objectWrapper.getClass().getSuperclass();
                Field mActive = classObserverWrapper.getDeclaredField("mActive");
                mActive.setAccessible(true);
                mActive.set(objectWrapper, active);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class ObserverWrapper<T> implements Observer<T> {

        private Observer<T> observer;

        public ObserverWrapper(Observer<T> observer) {
            this.observer = observer;
        }

        @Override
        public void onChanged(@Nullable T t) {
            if (observer != null) {
                if (isCallOnObserve()) {
                    return;
                }
                observer.onChanged(t);
            }
        }

        private boolean isCallOnObserve() {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            if (stackTrace != null && stackTrace.length > 0) {
                for (StackTraceElement element : stackTrace) {
                    if ("android.arch.lifecycle.LiveData".equals(element.getClassName()) &&
                            "observeForever".equals(element.getMethodName())) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}