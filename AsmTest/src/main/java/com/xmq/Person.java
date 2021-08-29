package com.xmq;


import com.xmq.thread.DelegateThread;
import com.xmq.thread.DelegateThreadPoolExecutor;

import java.lang.Thread;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Person {
    public String mName;
    public String mEnName;
    public int mAge;
    public void print() {
        String info = "dd";
        Log.d("Person", "print==="+info);
        System.out.println("print() invoke");
        new ThreadPoolExecutor(
                1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>()).execute(new Runnable() {
            //        Executors.newFixedThreadPool(1).execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("Executors newFixedThreadPool run()" + Thread.currentThread().getName());
            }
        });
    }

    public void print(Exception ex) {
        String info = "dd";
        Log.d("Person", "print==="+info ,ex);
        print();
        System.out.println("print() invoke");
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("new Runnable run()" + Thread.currentThread().getName());
            }
        }).start();
    }
    public StringBuffer print(StringBuffer buffer) {
        buffer.append("Name: ");
        buffer.append(mName);
        buffer.append("EnName: ");
        buffer.append(mEnName);
        System.out.println("print: ");
        Log.d("Person", "print===");
        return buffer;
    }

    @Override
    public String toString() {
        String info = "dd";
        Log.d("Person", "toString==="+info, new IllegalArgumentException(""));
        return "Person{" +
                "mName='" + mName + '\'' +
                ", mEnName='" + mEnName + '\'' +
                ", mAge=" + mAge +
                '}';
    }

}
