package com.zzw.distribution.lock.core.tick;

import com.zzw.distribution.lock.core.DistributedLock;
import com.zzw.distribution.lock.core.LockExecutors;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 心跳线程
 *
 * @author zhaozhiwei
 * @date 2019/5/31 17:51
 */
public class Tick implements Runnable {

    private String tickName;
    private String lockName;
    private Long delay;
    private TimeUnit timeUnit;
    private ScheduledExecutorService executor;
    private int executeTimes = 0;
    private LocalDateTime rightRunTime;
    private DistributedLock distributedLock;
    private volatile boolean interrupted = false;

    public Tick(String tickName, String lockName, Long delay, TimeUnit timeUnit, ScheduledExecutorService executor,
                LocalDateTime rightRunTime/*, DistributedLock distributedLock*/) {
        this.tickName = tickName;
        this.lockName = lockName;
        this.delay = delay;
        this.timeUnit = timeUnit;
        this.executor = executor;
        this.rightRunTime = rightRunTime;
//        this.distributedLock = distributedLock;
    }

    public void interrupt() {
        this.interrupted = true;
    }

    public void release() {
        distributedLock.unlock(lockName);
    }

    private void extend() {
        distributedLock.extend(lockName);
    }

    @Override
    public void run() {
        if (interrupted) {
            System.out.println("线程名称：" + tickName + "被通知中断！！！");
            return;
        }
        executeTimes++;
        // 模拟发送心跳
        extend();
        LocalDateTime now = LocalDateTime.now();
        long interval = Duration.between(rightRunTime, now).toMillis();
        System.out.println("执行时间：" + now.toString() + "，应执行时间：" + rightRunTime + "，执行间隔：" + interval
                + "毫秒，执行心跳线程名称：" + tickName + "，执行次数：" + executeTimes);
        this.rightRunTime = now.plusSeconds(2L);
        executor.schedule(this, delay, timeUnit);
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 500; i++) {
            int nextTime = new Random().nextInt(2000);
            Thread.sleep(nextTime);
            String tickName = "tick-thread-" + i;
            LocalDateTime now = LocalDateTime.now();
            Tick lockTick = new Tick(tickName, tickName, 2L, TimeUnit.SECONDS, LockExecutors.scheduledExecutorService, now.plusSeconds(2L));
            LockExecutors.scheduledExecutorService.schedule(lockTick, 2L, TimeUnit.SECONDS);
            LockExecutors.ticks.put(tickName, lockTick);
        }
        for (int i = 0; i < 10; i++) {
            Thread.sleep(10000);
            String tickName = "tick-thread-" + new Random().nextInt(50);
            Tick currentTick = LockExecutors.ticks.get(tickName);
            currentTick.interrupt();
        }
    }
}
