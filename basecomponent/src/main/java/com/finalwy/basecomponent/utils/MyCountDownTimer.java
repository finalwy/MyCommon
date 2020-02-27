package com.finalwy.basecomponent.utils;

import android.os.CountDownTimer;
import android.widget.Button;

/**
 * 验证码倒计时及重新发送验证码的工具类
 *
 * @author wy
 * @Date 2020-02-18
 */
public class MyCountDownTimer extends CountDownTimer {
    private Button mBtn;
    private int mEnable;
    private int mDisable;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public MyCountDownTimer(long millisInFuture, long countDownInterval, Button btn, int enable, int disable) {
        super(millisInFuture, countDownInterval);
        mBtn = btn;
        mEnable = enable;
        mDisable = disable;
    }

    //计时过程
    @Override
    public void onTick(long l) {
        //防止计时过程中重复点击
        mBtn.setClickable(false);
        mBtn.setText(l / 1000 + "s");
        //设置按钮背景色
        mBtn.setBackgroundResource(mDisable);
    }

    //计时完毕的方法
    @Override
    public void onFinish() {
        //重新给Button设置文字
        mBtn.setText("重新获取验证码");
        //设置可点击
        mBtn.setClickable(true);
        //设置按钮背景色
        mBtn.setBackgroundResource(mEnable);
    }
}