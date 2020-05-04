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
package com.zzw.distribution.lock.core.synchronizer;

import com.zzw.distribution.lock.core.LockExecutors;
import com.zzw.distribution.lock.core.tick.Tick;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.util.Pool;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * 基于 Redis 的锁
 *
 * @author zhaozhiwei
 * @date 2019/5/29 15:08
 */
public class RedisNonfairSynchronizer extends AbstractSynchronizer implements ZlockSynchronizer {

    private final Pool<Jedis> jedisPool;

    public RedisNonfairSynchronizer(Pool<Jedis> jedisPool, String lockName) {
        this.lockName = lockName;
        this.jedisPool = jedisPool;
    }

    @Override
    public boolean release(int arg) {
        try (Jedis jedis = getResource()) {
            String releaseLock =
                    "if (redis.call('exists', KEYS[1]) == 1) and (redis.call('get', KEYS[1]) == ARGV[1]) then" +
                    "    redis.call('del', KEYS[1]);" +
                    "end;" +
                    "return 1;";
            boolean success = Objects.equals(String.valueOf(jedis.eval(releaseLock, getKeys(), getArgs())), "1");
            if (success) {
                Tick tick = LockExecutors.getTicks().get(lockName);
                if (tick != null) {
                    tick.interrupt();
                }
                LockExecutors.getTicks().remove(lockName);
            }
        }
        return true;
    }

    @Override
    public boolean tryAcquire(int arg) {
        boolean success;
        try (Jedis jedis = getResource()) {
            String acquireLock =
                    "if (redis.call('exists', KEYS[1]) == 0) then " +
                    "    redis.call('set', KEYS[1], ARGV[1]); " +
                    "    redis.call('expire', KEYS[1], ARGV[2]); " +
                    "    return 1; " +
                    "else " +
                    "    return 0; " +
                    "end; ";
            success = Objects.equals(String.valueOf(jedis.eval(acquireLock, getKeys(), getArgs())), "1");
        }
        if (success) {
            addTask(lockName);
        }
        return success;
    }

    @Override
    public void extend() {
        try (Jedis jedis = getResource()) {
            String acquireLock =
                    "if (redis.call('exists', KEYS[1] == 1) and (redis.call('get', KEYS[1]) == ARGV[1]) then" +
                    "    redis.call('expire', KEYS[1], ARGV[3]);" +
                    "end;" +
                    "return 1;";
            boolean success = Objects.equals(String.valueOf(jedis.eval(acquireLock)), "1");
        }
    }

    private List<String> getKeys(){
        List<String> keys = new LinkedList<>();
        keys.add(lockName);
        return keys;
    }

    private List<String> getArgs(){
        List<String> args = new LinkedList<>();
        args.add(uuid);
        args.add(initTime + "");
        args.add(extendTime + "");
        return args;
    }

    private Jedis getResource() {
        return jedisPool.getResource();
    }

}

