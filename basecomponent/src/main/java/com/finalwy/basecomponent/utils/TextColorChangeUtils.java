package com.finalwy.basecomponent.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

/**
 * 文字变色工具类
 *
 * @author wy
 * @Date 2020-02-18
 */
public class TextColorChangeUtils {
    /***
     *通过Resources内的颜色实现文字变色龙
     *
     * @param context 上下文
     * @param textView 文字变色控件
     * @param textColor  文字固有颜色
     * @param startColor 开始半段文字color
     * @param endColor 结束半段 文字color
     * @param startStart 前半段开始变色文字下标
     * @param startEnd 前半段结束变色文字下标
     * @param endStart 后半段开始变色文字下标
     * @param endEnd 后半段结束变色文字下标
     * @param text 变色的文字内容
     * @return 返回变色结果
     */
    public static TextView interTextColorForResources(Context context, TextView textView, int textColor, int startColor,
                                                      int endColor, int startStart, int startEnd, int endStart, int endEnd, String text) {
        textView.setTextColor(context.getResources().getColor(textColor));
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        style.setSpan(new ForegroundColorSpan(context.getResources().getColor(startColor)), startStart, startEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ForegroundColorSpan(context.getResources().getColor(endColor)), endStart, endEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(style);
        return textView;
    }

    /**
     * 通过 ParseColor 的形式实现变色
     *
     * @param textView   文字变色控件
     * @param textColor  文字固有颜色
     * @param startColor 开始半段文字color
     * @param endColor   结束半段文字color
     * @param startStart 前半段开始变色文字下标
     * @param startEnd   后半段开始变色文字下标
     * @param endStart   后半段结束变色文字下标
     * @param endEnd     后半段结束变色文字下标
     * @param text       变色的文字内容
     * @return 返回变色结果
     */
    public static TextView interTextColorForParseColor(TextView textView, String textColor, String startColor, String endColor, int startStart, int startEnd, int endStart, int endEnd, String text) {
        textView.setTextColor(Color.parseColor(textColor));
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        style.setSpan(new ForegroundColorSpan(Color.parseColor(startColor)), startStart, startEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ForegroundColorSpan(Color.parseColor(endColor)), endStart, endEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return textView;
    }

}
