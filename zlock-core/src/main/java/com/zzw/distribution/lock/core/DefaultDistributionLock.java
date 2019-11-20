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

    public DefaultDistributionLock(Source source){
        this.source = source;
    }

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
