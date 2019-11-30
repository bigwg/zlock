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
package com.zzw.distribution.lock.core.source;

import com.zzw.distribution.lock.core.LockExecutors;
import com.zzw.distribution.lock.core.tick.Tick;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.commands.JedisCommands;
import redis.clients.jedis.util.Pool;

import java.util.Objects;

/**
 * 基于 Redis 的锁
 *
 * @author zhaozhiwei
 * @date 2019/5/29 15:08
 */
public class RedisSource extends AbstractSource implements Source {

    private Pool jedisPool;

    public RedisSource(String host, int port) {
        this(host, port, null);
    }

    public RedisSource(String host, int port, String password) {
        super();
        this.jedisPool = new JedisPool(new GenericObjectPoolConfig(), host, port, 3000, password);
    }

    public RedisSource(Pool pool) {
        super();
        this.jedisPool = pool;
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
            // return resource
            if (jedis instanceof ShardedJedis) {
                ((ShardedJedis) jedis).close();
            } else if (jedis instanceof Jedis) {
                ((Jedis) jedis).close();
            }
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
            // return resource
            if (jedis instanceof ShardedJedis) {
                ((ShardedJedis) jedis).close();
            } else if (jedis instanceof Jedis) {
                ((Jedis) jedis).close();
            }
        }
        if (result) {
            addTask(lockName);
        }
        return result;
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
            // return resource
            if (jedis instanceof ShardedJedis) {
                ((ShardedJedis) jedis).close();
            } else if (jedis instanceof Jedis) {
                ((Jedis) jedis).close();
            }
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

