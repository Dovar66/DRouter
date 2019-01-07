package com.dovar.router_api.router.ui.forresult;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * @Date: 2018/9/28
 * @Author: heweizong
 * @Description:
 */
public class Activity4ResultUtil {

    /**
     * @param intent   目标页面和携带参数
     * @param callback 目标页面setResult()之后会回调callback的onActivityResult()
     */
    public static void startForResult(FragmentActivity mActivity, Intent intent, Callback callback) {
        FragmentManager fm = mActivity.getSupportFragmentManager();
        Activity4ResultFragment mFragment = (Activity4ResultFragment) fm.findFragmentByTag(Activity4ResultFragment.class.getCanonicalName());
        if (mFragment == null) {
            mFragment = new Activity4ResultFragment();
            fm.beginTransaction().add(mFragment, Activity4ResultFragment.class.getCanonicalName()).commitAllowingStateLoss();
            fm.executePendingTransactions();
        }
        mFragment.startForResult(intent, callback);
    }
}
