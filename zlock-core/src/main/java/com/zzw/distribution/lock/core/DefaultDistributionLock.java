package com.zzw.distribution.lock.core;

import com.zzw.distribution.lock.core.source.Source;

import java.util.concurrent.TimeUnit;

/**
 * 默认分布式锁实现
 *
 * @author zhaozhiwei
 * @date 2019/6/1 21:39
 */
public class DefaultDistributionLock implements DistributedLock {

    private Source source;

    @Override
    public void lock(String lockName) {
        source.acquire(lockName, 1);
    }

    @Override
    public void lockInterruptibly(String lockName) throws InterruptedException {
        source.acquireInterruptibly(lockName, 1);
    }

    @Override
    public boolean tryLock(String lockName) {
        return source.tryAcquire(lockName, 1);
    }

    @Override
    public boolean tryLock(String lockName, long time, TimeUnit unit) throws InterruptedException {
        return source.tryAcquireNanos(lockName, 1, unit.toNanos(time));
    }

    @Override
    public void unlock(String lockName) {
        source.release(lockName, 1);
    }

    @Override
    public void extend(String lockName) {
        source.extend(lockName);
    }
}
