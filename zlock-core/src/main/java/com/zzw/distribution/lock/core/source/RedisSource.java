package com.zzw.distribution.lock.core.source;

import com.zzw.distribution.lock.core.LockExecutors;
import com.zzw.distribution.lock.core.tick.Tick;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 基于 Redis 的锁
 *
 * @author zhaozhiwei
 * @date 2019/5/29 15:08
 */
public class RedisSource implements Source {

    private JedisPool jedisPool;
    private String localIp;
    private int initTime;
    private int extendTime;

    public RedisSource(String host, int port) {
        this.jedisPool = new JedisPool(host, port);
        this.initTime = 10;
        this.extendTime = 3;
        try {
            this.localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            this.localIp = "0.0.0.0";
        }
    }

    @Override
    public void acquire(String lockName, int arg) {
        if (!tryAcquire(lockName, arg)) {
            for (; ; ) {
                if (tryAcquire(lockName, arg)) {
                    return;
                } else {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    @Override
    public void acquireInterruptibly(String lockName, int arg) throws InterruptedException {

    }

    @Override
    public void acquireShared(String lockName, int arg) {

    }

    @Override
    public void acquireSharedInterruptibly(String lockName, int arg) throws InterruptedException {

    }

    @Override
    public boolean release(String lockName, int arg) {
        Jedis jedis = getResource();
        try {
            String value = jedis.get(lockName);
            if (value != null) {
                if (Objects.equals(value, localIp)) {
                    jedis.del(lockName);
                    Tick tick = LockExecutors.ticks.get(lockName);
                    if (tick != null){

                    }
                }
            }
        } finally {
            jedisPool.close();
        }
        return true;
    }

    @Override
    public boolean releaseShared(String lockName, int arg) {
        return false;
    }

    @Override
    public boolean tryAcquire(String lockName, int arg) {
        Jedis jedis = getResource();
        boolean result;
        try {
            result = setnx(jedis, lockName);
        } finally {
            jedisPool.close();
        }
        if (result) {
            LocalDateTime now = LocalDateTime.now();
            Tick lockTick = new Tick(lockName, lockName, extendTime, TimeUnit.SECONDS, LockExecutors.scheduledExecutorService,
                    now.plusSeconds(2L), this);
            LockExecutors.scheduledExecutorService.schedule(lockTick, extendTime, TimeUnit.SECONDS);
            LockExecutors.ticks.put(lockName, lockTick);
        }
        return result;
    }

    @Override
    public boolean tryAcquireNanos(String lockName, int arg, long nanosTimeout) {
        return false;
    }

    @Override
    public boolean tryAcquireSharedNanos(String lockName, int arg, long nanosTimeout) {
        return false;
    }

    @Override
    public void extend(String lockName) {
        Jedis jedis = getResource();
        try {
            String value = jedis.get(lockName);
            if (value != null) {
                if (Objects.equals(value, localIp)) {
                    jedis.expire(lockName, extendTime);
                }
            }
        } finally {
            jedisPool.close();
        }
    }

    private Jedis getResource() {
        return jedisPool.getResource();
    }

    private boolean setnx(Jedis jedis, String lockName) {
        long result = jedis.setnx(lockName, localIp);
        jedis.expire(lockName, initTime);
        return result != 0;
    }
}
