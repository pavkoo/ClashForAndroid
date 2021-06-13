package com.github.kr328.clash.common.util;

import android.util.Base64;

/**
 * @author shangji_cd
 */
public class StringUtil {
    public static String base64(String value) {
        return Base64.encodeToString(value.getBytes(), Base64.NO_WRAP);
    }
}
