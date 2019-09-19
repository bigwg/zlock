package com.zzw.distribution.lock.core.source;

import com.zzw.distribution.lock.core.LockExecutors;
import com.zzw.distribution.lock.core.tick.Tick;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPool;
import redis.clients.util.Pool;

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

    private Pool jedisPool;
    private String localIp;
    private int initTime;
    private int extendTime;

    public RedisSource(String host, int port) {
        this(host, port, null);
    }

    public RedisSource(String host, int port, String password) {
        this.jedisPool = new JedisPool(new GenericObjectPoolConfig(), host, port, 3000, password);
        this.initTime = 10;
        this.extendTime = 5;
        try {
            this.localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            this.localIp = "0.0.0.0";
        }
    }

    public RedisSource(Pool pool) {
        this.jedisPool = pool;
        this.initTime = 10;
        this.extendTime = 5;
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
        JedisCommands jedis = getResource();
        try {
            String value = jedis.get(lockName);
            if (value != null) {
                if (Objects.equals(value, localIp)) {
                    jedis.del(lockName);
                    Tick tick = LockExecutors.getTicks().get(lockName);
                    if (tick != null) {
                        tick.interrupt();
                    }
                    LockExecutors.getTicks().remove(lockName);
                }
            }
        } finally {
            jedisPool.returnResource(jedis);
        }
        return true;
    }

    @Override
    public boolean releaseShared(String lockName, int arg) {
        return false;
    }

    @Override
    public boolean tryAcquire(String lockName, int arg) {
        JedisCommands jedis = getResource();
        boolean result;
        try {
            result = setnx(jedis, lockName);
        } finally {
            jedisPool.returnResource(jedis);
        }
        if (result) {
            LocalDateTime now = LocalDateTime.now();
            Tick lockTick = new Tick(lockName, lockName, extendTime - 2, TimeUnit.SECONDS, LockExecutors.getScheduledExecutorService(),
                    now.plusSeconds(2L), this);
            LockExecutors.getScheduledExecutorService().schedule(lockTick, extendTime, TimeUnit.SECONDS);
            LockExecutors.getTicks().put(lockName, lockTick);
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
        JedisCommands jedis = getResource();
        try {
            String value = jedis.get(lockName);
            if (value != null) {
                if (Objects.equals(value, localIp)) {
                    jedis.expire(lockName, extendTime);
                }
            }
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    private JedisCommands getResource() {
        return (JedisCommands) jedisPool.getResource();
    }

    private boolean setnx(JedisCommands jedis, String lockName) {
        long result = jedis.setnx(lockName, localIp);
        jedis.expire(lockName, initTime);
        return result != 0;
    }

}

