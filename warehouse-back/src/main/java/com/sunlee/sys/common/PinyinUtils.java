package com.sunlee.sys.common;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import lombok.extern.slf4j.Slf4j;

/**
 * 拼音工具类
 *
 * @Author: sunlee
 * @Date: 2026/03/15 10:29
 */
public class PinyinUtils {

    /**
     *  返回一个拼音字符串
     */
    public static String getPingYin(String inputString) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        StringBuilder output = new StringBuilder();
        if (inputString != null && inputString.length() > 0 && !"null".equals(inputString)) {
            char[] input = inputString.trim().toCharArray();
            try {
                for (char c : input) {
                    if (Character.toString(c).matches("[\\u4E00-\\u9FA5]+")) {
                        String[] temp = PinyinHelper.toHanyuPinyinStringArray(c, format);
                        if (temp != null && temp.length > 0) {
                            output.append(temp[0]);
                        }
                    } else {
                        output.append(c);
                    }
                }
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                log.error("拼音转换失败: {}", e.getMessage(), e);
            }
        } else {
            return "";
        }
        return output.toString();
    }

    /**
     * 获取拼音首字母简写（如 "可口可乐" → "kkkl"）
     */
    public static String getAbbreviation(String inputString) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        StringBuilder output = new StringBuilder();
        if (inputString != null && inputString.length() > 0 && !"null".equals(inputString)) {
            char[] input = inputString.trim().toCharArray();
            try {
                for (char c : input) {
                    if (Character.toString(c).matches("[\\u4E00-\\u9FA5]+")) {
                        String[] temp = PinyinHelper.toHanyuPinyinStringArray(c, format);
                        if (temp != null && temp.length > 0) {
                            output.append(temp[0].charAt(0));
                        }
                    } else {
                        output.append(Character.toLowerCase(c));
                    }
                }
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                log.error("拼音简写转换失败: {}", e.getMessage(), e);
            }
        } else {
            return "";
        }
        return output.toString();
    }

    public static void main(String[] args) {
        String s = getPingYin("超级管理员");
    }


}
