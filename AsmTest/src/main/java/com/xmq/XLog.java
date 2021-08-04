package com.xmq;

public class XLog {
    public static void d(String tag, String msg) {
        System.out.println("XDEBUG/"+tag+": "+msg);
    }
    public static void i(String tag, String msg) {
        System.out.println("XINFO/"+tag+": "+msg);
    }
    public static void w(String tag, String msg) {
        System.out.println("XWARN/"+tag+": "+msg);
    }
}
