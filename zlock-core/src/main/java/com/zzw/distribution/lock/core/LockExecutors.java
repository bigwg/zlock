package com.zzw.distribution.lock.core;

import com.zzw.distribution.lock.core.tick.Tick;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 锁线程池
 *
 * @author zhaozhiwei
 * @date 2019/5/31 18:01
 */
public class LockExecutors {

    public static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() + 1;

    private static Map<String, Tick> ticks = new ConcurrentHashMap<>(20);

    private static ScheduledExecutorService scheduledExecutorService;

    public static Map<String, Tick> getTicks() {
        return ticks;
    }

    public static ScheduledExecutorService getScheduledExecutorService() {
        if (scheduledExecutorService != null) {
            return scheduledExecutorService;
        } else {
            synchronized (LockExecutors.class) {
                if (scheduledExecutorService == null) {
                    scheduledExecutorService = new ScheduledThreadPoolExecutor(CORE_POOL_SIZE,
                            new LockThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
                }
                return scheduledExecutorService;
            }
        }
    }

    static class LockThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        LockThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "lock-pool-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }
}
