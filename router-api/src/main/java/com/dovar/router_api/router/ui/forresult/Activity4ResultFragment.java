package com.dovar.router_api.router.ui.forresult;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.SparseArray;

/**
 * @Date: 2018/9/28
 * @Author: heweizong
 * @Description: 用于处理startActivityForResult的请求
 */
public class Activity4ResultFragment extends Fragment {
    private final SparseArray<Callback> mCallbacks = new SparseArray<>();
    private int countCode;//不用callback.hashCode()是因为requestCode要求低于16位

    public void startForResult(Intent intent, Callback callback) {
        final int requestCode = countCode;
        countCode++;
        mCallbacks.put(requestCode, callback);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Callback mCallback = mCallbacks.get(requestCode);
        mCallbacks.remove(requestCode);
        if (mCallback != null) {
            mCallback.onActivityResult(resultCode, data);
        }
    }
}
