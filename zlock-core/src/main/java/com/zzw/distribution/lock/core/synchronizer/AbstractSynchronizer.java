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
import com.zzw.distribution.lock.core.constant.ZlockConstants;
import com.zzw.distribution.lock.core.tick.Tick;

import javax.sql.rowset.spi.SyncResolver;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * 资源抽象类
 *
 * @author zhaozhiwei
 * @date 2019/12/1 12:32 上午
 */
public abstract class AbstractSynchronizer implements ZlockSynchronizer {

    protected int initTime;
    protected int extendTime;
    protected String lockName;
    protected String uuid;
    protected static final String BASE_LOCK_DIR = "/zlock";
    protected static final String SEPARATOR = "/";
    protected final AtomicInteger state;
    /**
     * 独占的线程
     */
    private transient Thread exclusiveOwnerThread;

    public AbstractSynchronizer() {
        this.initTime = 10;
        this.extendTime = 5;
        uuid = UUID.randomUUID().toString();
        state = new AtomicInteger(0);
    }

    @Override
    public void acquire(int arg) {
        if (tryAcquire(arg)) {
            return;
        }
        for (; ; ) {
            if (tryAcquire(arg)) {
                return;
            } else {
                LockSupport.parkNanos(this, TimeUnit.MILLISECONDS.toNanos(ZlockConstants.SPIN_AWAIT_TIME));
            }
        }
    }

    @Override
    public void acquireInterruptibly(int arg) throws InterruptedException {
        checkInterrupted();
        if (tryAcquire(arg)) {
            return;
        }
        for (; ; ) {
            checkInterrupted();
            if (tryAcquire(arg)) {
                return;
            } else {
                LockSupport.parkNanos(this, TimeUnit.MILLISECONDS.toNanos(ZlockConstants.SPIN_AWAIT_TIME));
            }
        }
    }

    @Override
    public void acquireShared(int arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void acquireSharedInterruptibly(int arg) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryAcquire(int arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryAcquireNanos(int arg, long nanosTimeout) {
        return tryAcquire(arg) || doAcquireNanos(arg, nanosTimeout);
    }

    @Override
    public boolean releaseShared(int arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryAcquireSharedNanos(int arg, long nanosTimeout) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setExclusiveOwnerThread(Thread thread) {
        exclusiveOwnerThread = thread;
    }

    @Override
    public final Thread getExclusiveOwnerThread() {
        return exclusiveOwnerThread;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    protected void addTask(String lockName) {
        LocalDateTime now = LocalDateTime.now();
        Tick lockTick = new Tick(lockName, lockName, extendTime - 2, TimeUnit.SECONDS, LockExecutors.getScheduledExecutorService(),
                now.plusSeconds(2L), this);
        LockExecutors.getScheduledExecutorService().schedule(lockTick, extendTime, TimeUnit.SECONDS);
        LockExecutors.getTicks().put(lockName, lockTick);
    }

    protected boolean doAcquireNanos(int arg, long nanosTimeout) {
        if (nanosTimeout <= 0L) {
            return false;
        }
        final long deadline = System.nanoTime() + nanosTimeout;
        for (; ; ) {
            if (tryAcquire(arg)) {
                return true;
            } else {
                LockSupport.parkNanos(this, TimeUnit.MILLISECONDS.toNanos(ZlockConstants.SPIN_AWAIT_TIME));
            }
            nanosTimeout = deadline - System.nanoTime();
            if (nanosTimeout <= 0L) {
                return false;
            }
        }
    }

    private void checkInterrupted() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }

}
