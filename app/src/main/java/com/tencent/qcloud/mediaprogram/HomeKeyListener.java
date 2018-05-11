package com.tencent.qcloud.mediaprogram;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class HomeKeyListener {
    static final String TAG = "HomeKeyListener";

    private Context mContext;
    private IntentFilter mHomeKeyFilter;
    private OnHomePressedListener mListener;
    private InnerReceiver mReceiver;

    // 回调接口
    public interface OnHomePressedListener {
        void onHomePressed();
        void onHomeLongPressed();
    }

    public HomeKeyListener(Context context) {
        mContext = context;
    }

    /**
     * 设置监听
     *
     * @param listener
     */
    public void setOnHomePressedListener(OnHomePressedListener listener) {
        mListener = listener;
        mReceiver = new InnerReceiver();
    }

    /**
     * 开始监听，注册广播
     */
    public void registerHomeReceive() {
        if (mReceiver != null) {
            mHomeKeyFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            mContext.registerReceiver(mReceiver, mHomeKeyFilter);
        }
    }

    /**
     * 停止监听，注销广播
     */
    public void unregisterHomeReceiver() {
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }
    }

    class InnerReceiver extends BroadcastReceiver {
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        final String FINISH = "finish";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("XIAO", action + "|" + intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY));
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    if (mListener != null) {
                        //TODO : see FutureBox
                        if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                            // 短按home键
                            mListener.onHomePressed();
                        } else if (reason
                                .equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                            // 长按home键
                            mListener.onHomeLongPressed();
                        }
                    }
                }
            } else if (action.equals(FINISH)) {
                // 短按home键
                mListener.onHomePressed();
            }
        }
    }
}