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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 资源抽象类
 *
 * @author zhaozhiwei
 * @date 2019/12/1 12:32 上午
 */
public abstract class AbstractSource implements Source {

    protected int initTime;
    protected int extendTime;
    protected String localIp;
    protected static final String BASE_LOCK_DIR = "/zlock";
    protected static final String SEPARATOR = "/";

    public AbstractSource() {
        this.initTime = 10;
        this.extendTime = 5;
        try {
            this.localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            this.localIp = UUID.randomUUID().toString();
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
        if (!tryAcquire(lockName, arg)) {
            for (; ; ) {
                if (tryAcquire(lockName, arg)) {
                    return;
                } else {
                    Thread.sleep(50);
                }
            }
        }
    }

    @Override
    public void acquireShared(String lockName, int arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void acquireSharedInterruptibly(String lockName, int arg) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryAcquire(String lockName, int arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryAcquireNanos(String lockName, int arg, long nanosTimeout) {
        return tryAcquire(lockName, arg) || doAcquireNanos(lockName, arg, nanosTimeout);
    }

    @Override
    public boolean releaseShared(String lockName, int arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryAcquireSharedNanos(String lockName, int arg, long nanosTimeout) {
        return false;
    }

    protected void addTask(String lockName) {
        LocalDateTime now = LocalDateTime.now();
        Tick lockTick = new Tick(lockName, lockName, extendTime - 2, TimeUnit.SECONDS, LockExecutors.getScheduledExecutorService(),
                now.plusSeconds(2L), this);
        LockExecutors.getScheduledExecutorService().schedule(lockTick, extendTime, TimeUnit.SECONDS);
        LockExecutors.getTicks().put(lockName, lockTick);
    }

    protected boolean doAcquireNanos(String lockName, int arg, long nanosTimeout) {
        if (nanosTimeout <= 0L) {
            return false;
        }
        final long deadline = System.nanoTime() + nanosTimeout;
        for (; ; ) {
            if (tryAcquire(lockName, arg)) {
                return true;
            } else {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
            }
            nanosTimeout = deadline - System.nanoTime();
            if (nanosTimeout <= 0L) {
                return false;
            }
        }
    }
}
