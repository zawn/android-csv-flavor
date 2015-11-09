/*
 * Copyright 2015 ZhangZhenli <zhangzhenli@live.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.house365.build.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 汉字转拼音,驼峰格式.
 *
 * @author ZhangZhenli <zhangzhenli@live.com>
 */
public class HanziToPinyin {

    public static String toPinYin(String hzString) {
        /**
         * 设置输出格式
         */
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        char[] hzChars = new char[hzString.length()];
        for (int i = 0; i < hzString.length(); i++) {
            hzChars[i] = hzString.charAt(i);
        }
        int t0 = hzChars.length;
        StringBuilder sb = new StringBuilder();
        try {
            for (int i = 0; i < t0; i++) {
                char c = hzChars[i];
                String[] result = PinyinHelper.toHanyuPinyinStringArray(c, format);
                if (result != null && result.length > 0) {
                    // 去除Guava依赖
                    //sb.append(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, result[0]));
                    String s = result[0];// 不管多音字,只取第一个  
                    char fl = s.charAt(0);// 大写第一个字母  
                    String pinyin = String.valueOf(fl).toUpperCase().concat(s.substring(1));
                    sb.append(pinyin);
                } else {
                    sb.append(c);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
