package com.servicenow.skilledserviceapp.utils;

import android.util.Log;

public class LogUtils {
    private static boolean debug = Constants.PRINT_LOGS;

    public static void d(String str, String str2) {
        if (debug)
            Log.d(str, str2);
    }

    public static void e(String str, String str2) {
        if (debug)
            Log.e(str, str2);
    }

    public static void i(String str, String str2) {
        if (debug)
            Log.i(str, str2);
    }

    public static void w(String str, String str2) {
        if (debug)
            Log.w(str, str2);
    }

    public static void v(String str, String str2) {
        if (debug)
            Log.v(str, str2);
    }
}
