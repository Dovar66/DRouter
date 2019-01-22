package com.example.common_base;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

import com.dovar.dtoast.DToast;
import com.dovar.dtoast.inner.IToast;

public class ToastUtil {

    public static void show(Context mContext, String msg) {
        if (mContext == null || msg == null) return;
        IToast toast = DToast.make(mContext);
        TextView tv_text = (TextView) toast.getView().findViewById(R.id.tv_content);
        if (tv_text != null) {
            tv_text.setText(msg);
        }
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 30).show();
    }
}
