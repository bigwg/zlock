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

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.util.Pool;

/**
 * redis source
 *
 * @author zhaozhiwei
 * @since 2020/5/3
 */
public class RedisSource {

    private final Pool<Jedis> jedisPool;

    public RedisSource(String host, int port) {
        this(host, port, null);
    }

    public RedisSource(String host, int port, String password) {
        super();
        this.jedisPool = new JedisPool(new GenericObjectPoolConfig(), host, port, 3000, password);
    }

    public RedisSource(Pool<Jedis> pool) {
        super();
        this.jedisPool = pool;
    }

    public Pool<Jedis> getJedisPool() {
        return jedisPool;
    }
}
