package com.zzw.distribution.lock.core;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁
 *
 * @author zhaozhiwei
 * @date 2019/5/29 12:12
 */
public interface DistributedLock {

    void lock(String lockName);

    void lockInterruptibly(String lockName) throws InterruptedException;

    boolean tryLock(String lockName);

    boolean tryLock(String lockName, long time, TimeUnit unit) throws InterruptedException;

    void unlock(String lockName);

    void extend(String lockName);
}
