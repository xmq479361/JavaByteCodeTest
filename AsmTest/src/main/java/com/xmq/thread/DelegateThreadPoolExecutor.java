package com.xmq.thread;

import java.util.concurrent.*;

public class DelegateThreadPoolExecutor extends ThreadPoolExecutor {
    private String invokeMethodName;

    public DelegateThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                      BlockingQueue<Runnable> workQueue, String invokeMethodName) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new NamedThreadFactory(invokeMethodName));
        this.invokeMethodName = invokeMethodName;
    }

    public DelegateThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                      BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, String invokeMethodName) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new NamedThreadFactory(threadFactory, invokeMethodName));
        this.invokeMethodName = invokeMethodName;
    }

    public DelegateThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                      BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler, String invokeMethodName) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new NamedThreadFactory(invokeMethodName), handler);
        this.invokeMethodName = invokeMethodName;
    }

    public DelegateThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                      BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler,
                                      String invokeMethodName) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new NamedThreadFactory(threadFactory, invokeMethodName), handler);
        this.invokeMethodName = invokeMethodName;
    }

    @Override
    public void execute(Runnable command) {
        Thread.currentThread().setName(invokeMethodName);
        super.execute(command);
    }
}
