package com.course.instagram.helper;

import android.util.Base64;

public class Base64Custom {

    public static String encodeEmail(String text) {
        return Base64.encodeToString(text.getBytes(), Base64.NO_WRAP).replace("(\\n|\\r)", "");
    }

    public static String decodeEmail(String text) {
        return Base64.decode(text, Base64.NO_WRAP).toString();
    }
}
