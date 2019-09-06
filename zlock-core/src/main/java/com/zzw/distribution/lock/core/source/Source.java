package com.zzw.distribution.lock.core.source;

/**
 * 分布式锁服务接口
 *
 * @author zhaozhiwei
 * @date 2019/5/29 14:13
 */
public interface Source {

    void acquire(String lockName, int arg);

    void acquireInterruptibly(String lockName, int arg) throws InterruptedException ;

    void acquireShared(String lockName, int arg);

    void acquireSharedInterruptibly(String lockName, int arg) throws InterruptedException ;

    boolean release(String lockName, int arg);

    boolean releaseShared(String lockName, int arg);

    boolean tryAcquire(String lockName, int arg);

    boolean tryAcquireNanos(String lockName, int arg, long nanosTimeout);

    boolean tryAcquireSharedNanos(String lockName, int arg, long nanosTimeout);
    
    void extend(String lockName);
}
