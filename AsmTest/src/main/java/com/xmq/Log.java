package com.xmq;

public class Log {

    public static void d(String tag, String msg) {
        System.out.println("DEBUG/"+tag+": "+msg);
    }
    public static void d(String tag, String msg,Exception ex) {
        System.out.println("DEBUG/"+tag+": "+msg+"=="+ex);
    }
    public static void i(String tag, String msg) {
        System.out.println("INFO/"+tag+": "+msg);
    }
    public static void w(String tag, String msg) {
        System.out.println("WARN/"+tag+": "+msg);
    }
}
