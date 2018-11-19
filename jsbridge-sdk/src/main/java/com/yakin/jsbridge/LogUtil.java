package com.yakin.jsbridge;

import android.util.Log;

import java.util.Locale;

public class LogUtil {

    private static String sTAG = "JsBridge:";
    public static boolean isDebug = false;

    public static void e(String format, Object... args) {
        Log.e(sTAG, buildMessage(format, args));
    }

    public static void e(Throwable e, String format, Object... args) {
        Log.e(sTAG, buildMessage(format, args), e);
    }

    public static void i(String format, Object... args) {
        Log.i(sTAG, buildMessage(format, args));
    }

    public static void i(Throwable e, String format, Object... args) {
        Log.i(sTAG, buildMessage(format, args), e);
    }

    public static void d(String format, Object... args) {
        if(isDebug) {
            Log.d(sTAG, buildMessage(format, args));
        }
    }

    public static void d(Throwable e, String format, Object... args) {
        if(isDebug) {
            Log.d(sTAG, buildMessage(format, args), e);
        }
    }

    private static String buildMessage(String format, Object... args) {
        String msg = (args == null || args.length < 1) ? format : String.format(Locale.US, format, args);
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();

        String caller = "<unknown>";
        for (StackTraceElement stack : trace) {
            String clazzName = stack.getClassName();
            if (!clazzName.equals(LogUtil.class.getName())) {
                clazzName = clazzName.substring(clazzName.lastIndexOf('.') + 1);

                caller = clazzName + "." + stack.getMethodName() + " (" + stack.getLineNumber() + ")";
                break;
            }
        }

        return String.format(Locale.US, "[%d] %s: %s", Thread.currentThread().getId(), caller, msg);
    }
}
