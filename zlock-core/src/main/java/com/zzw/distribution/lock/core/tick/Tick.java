/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zzw.distribution.lock.core.tick;

import com.zzw.distribution.lock.core.LockExecutors;
import com.zzw.distribution.lock.core.synchronizer.ZlockSynchronizer;

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
    private ZlockSynchronizer sync;
    private volatile boolean interrupted = false;

    public Tick(String tickName, String lockName, long delay, TimeUnit timeUnit, ScheduledExecutorService executor,
                LocalDateTime rightRunTime, ZlockSynchronizer sync) {
        this.tickName = tickName;
        this.lockName = lockName;
        this.delay = delay;
        this.timeUnit = timeUnit;
        this.executor = executor;
        this.rightRunTime = rightRunTime;
        this.sync = sync;
    }

    public void interrupt() {
        this.interrupted = true;
    }

    public void release() {
        sync.release(1);
    }

    private void extend() {
        sync.extend();
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
//        for (int i = 0; i < 500; i++) {
//            int nextTime = new Random().nextInt(2000);
//            Thread.sleep(nextTime);
//            String tickName = "tick-thread-" + i;
//            LocalDateTime now = LocalDateTime.now();
//            Source source = new NonfairRedisSource("127.0.0.1", 6379);
//            Tick lockTick = new Tick(tickName, tickName, 2L, TimeUnit.SECONDS, LockExecutors.getScheduledExecutorService(), now.plusSeconds(2L), source);
//            LockExecutors.getScheduledExecutorService().schedule(lockTick, 2L, TimeUnit.SECONDS);
//            LockExecutors.getTicks().put(tickName, lockTick);
//        }
//        for (int i = 0; i < 10; i++) {
//            Thread.sleep(10000);
//            String tickName = "tick-thread-" + new Random().nextInt(50);
//            Tick currentTick = LockExecutors.getTicks().get(tickName);
//            currentTick.interrupt();
//        }
    }
}
