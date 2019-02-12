package com.dovar.router_api.router.eventbus;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.dovar.router_api.router.eventbus.liveevent.LiveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 事件总线
 * note:
 * 要求：事件总线的key = 组件key + event名.
 * <p>
 * 为了防止事件滥用，在发送事件时需要检查事件类型是否为已注册的类型，如果不是则不允许发送。
 */
public class LiveEventBus {

    private final Map<String, BusLiveEvent<Bundle>> bus;

    private LiveEventBus() {
        bus = new HashMap<>();
    }

    private static class SingletonHolder {
        private static final LiveEventBus DEFAULT_BUS = new LiveEventBus();
    }

    public static LiveEventBus instance() {
        return SingletonHolder.DEFAULT_BUS;
    }

    public synchronized Observable<Bundle> with(String key) {
        if (!bus.containsKey(key)) {
            bus.put(key, new BusLiveEvent<Bundle>(key));
        }
        return bus.get(key);
    }

    public void subscribe(String key, LifecycleOwner owner, Observer<Bundle> observer) {
        if (TextUtils.isEmpty(key) || owner == null || observer == null) return;
        with(key).observe(owner, observer);
    }

    public void subscribeForever(String key, Observer<Bundle> observer) {
        if (TextUtils.isEmpty(key) || observer == null) return;
        with(key).observeForever(observer);
    }

    public void unsubscribe(String key, Observer<Bundle> observer) {
        if (TextUtils.isEmpty(key) || observer == null) return;
        if (bus.containsKey(key)) {
            with(key).removeObserver(observer);
        }
    }

    public void publish(String key, Bundle obj) {
        if (TextUtils.isEmpty(key)) return;
        //为了防止事件滥用，在发送事件时需要检查事件类型是否为已注册的类型，如果不是则不允许发送。
        if (bus.containsKey(key)) {
            with(key).postValue(obj);
        }
    }

    private boolean lifecycleObserverAlwaysActive = true;

    public void lifecycleObserverAlwaysActive(boolean active) {
        lifecycleObserverAlwaysActive = active;
    }

    public interface Observable<T> {
        void setValue(T value);

        void postValue(T value);

        void postValueDelay(T value, long delay);

        void postValueDelay(T value, long delay, TimeUnit unit);

        void observe(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer);

        void observeSticky(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer);

        void observeForever(@NonNull Observer<T> observer);

        void observeStickyForever(@NonNull Observer<T> observer);

        void removeObserver(@NonNull Observer<T> observer);
    }

    private class BusLiveEvent<T> extends LiveEvent<T> implements Observable<T> {

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
        private Handler mainHandler = new Handler(Looper.getMainLooper());

        private BusLiveEvent(@NonNull String key) {
            this.key = key;
        }

        @Override
        protected Lifecycle.State observerActiveLevel() {
            return lifecycleObserverAlwaysActive ? Lifecycle.State.CREATED : Lifecycle.State.STARTED;
        }

        @Override
        public void postValueDelay(T value, long delay) {
            mainHandler.postDelayed(new PostValueTask(value), delay);
        }

        @Override
        public void postValueDelay(T value, long delay, TimeUnit unit) {
            postValueDelay(value, TimeUnit.MILLISECONDS.convert(delay, unit));
        }

        @Override
        public void removeObserver(@NonNull Observer<T> observer) {
            super.removeObserver(observer);
            if (!hasObservers()) {
                LiveEventBus.instance().bus.remove(key);
            }
        }
    }
}