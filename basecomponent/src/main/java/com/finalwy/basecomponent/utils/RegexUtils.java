package com.finalwy.basecomponent.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则
 *
 * @author wy
 * @Date 2020-02-18
 */
public class RegexUtils {
    /**
     * 从字符串中提取数字
     *
     * @param str
     * @return
     */
    public static String extractNumberFromStr(String str) {
        String number = "";
        //正则：匹配非数字字符
        String regex = "[^0-9]";
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(str);
        //将匹配到的非数字字符替换为空格
        number = matcher.replaceAll("").trim();
        return number;
    }

    public static boolean isPassWord6To16(String password) {
        //判断密码是否是6-16为数字加字母
        String str = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";
        Pattern p = Pattern.compile(str);
        Matcher m1 = p.matcher(password);
        return m1.matches();

    }
}
