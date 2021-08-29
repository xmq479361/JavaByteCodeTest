package com.xmq.thread;

public class DelegateThread extends Thread {
    public String invokeMethodName;

    public DelegateThread(String invokeMethodName) {
        this.invokeMethodName = invokeMethodName;
    }

    public DelegateThread(String invokeMethodName, Runnable target) {
        super(target);
        this.invokeMethodName = invokeMethodName;
    }

    public DelegateThread(Runnable target, String invokeMethodName) {
        super(target);
        this.invokeMethodName = invokeMethodName;
    }

    public DelegateThread(ThreadGroup group, Runnable target, String invokeMethodName) {
        super(group, target);
        this.invokeMethodName = invokeMethodName;
    }

    public DelegateThread(String name, String invokeMethodName) {
        super(name);
        this.invokeMethodName = invokeMethodName;
    }

    public DelegateThread(ThreadGroup group, String name, String invokeMethodName) {
        super(group, name);
        this.invokeMethodName = invokeMethodName;
    }

    public DelegateThread(Runnable target, String name, String invokeMethodName) {
        super(target, name);
        this.invokeMethodName = invokeMethodName;
    }

    public DelegateThread(ThreadGroup group, Runnable target, String name, String invokeMethodName) {
        super(group, target, name);
        this.invokeMethodName = invokeMethodName;
    }

    public DelegateThread(ThreadGroup group, Runnable target, String name, long stackSize, String invokeMethodName) {
        super(group, target, name, stackSize);
        this.invokeMethodName = invokeMethodName;
    }

    public DelegateThread(ThreadGroup group, Runnable target, String name, long stackSize, boolean inheritThreadLocals, String invokeMethodName) {
        super(group, target, name, stackSize, inheritThreadLocals);
        this.invokeMethodName = invokeMethodName;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(invokeMethodName);
        super.run();
    }




    /**
     * {@code U+200B}: Zero-Width Space
     */
    static final String MARK = "\u200B";

    public static Thread newThread(final String prefix) {
        return new Thread(prefix);
    }

    public static Thread newThread(final Runnable target, final String prefix) {
        return new Thread(target, prefix);
    }

    public static Thread newThread(final ThreadGroup group, final Runnable target, final String prefix) {
        return new Thread(group, target, prefix);
    }

    public static Thread newThread(final String name, final String prefix) {
        return new Thread(makeThreadName(name, prefix));
    }

    public static Thread newThread(final ThreadGroup group, final String name, final String prefix) {
        return new Thread(group, makeThreadName(name, prefix));
    }

    public static Thread newThread(final Runnable target, final String name, final String prefix) {
        return new Thread(target, makeThreadName(name, prefix));
    }

    public static Thread newThread(final ThreadGroup group, final Runnable target, final String name, final String prefix) {
        return new Thread(group, target, makeThreadName(name, prefix));
    }

    public static Thread newThread(final ThreadGroup group, final Runnable target, final String name, final long stackSize, final String prefix) {
        return new Thread(group, target, makeThreadName(name, prefix), stackSize);
    }

    public static Thread setThreadName(final Thread t, final String prefix) {
        t.setName(makeThreadName(t.getName(), prefix));
        return t;
    }

    public static String makeThreadName(final String name) {
        return name == null ? "" : name.startsWith(MARK) ? name : (MARK + name);
    }

    public static String makeThreadName(final String name, final String prefix) {
        return name == null ? prefix : (name.startsWith(MARK) ? name : (prefix + "#" + name));
    }
}
