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

/**
 * 分布式锁服务接口
 *
 * @author zhaozhiwei
 * @date 2019/5/29 14:13
 */
public interface ZlockSynchronizer {

    void acquire(int arg);

    void acquireInterruptibly(int arg) throws InterruptedException;

    void acquireShared(int arg);

    void acquireSharedInterruptibly(int arg) throws InterruptedException;

    boolean release(int arg);

    boolean releaseShared(int arg);

    boolean tryAcquire(int arg);

    boolean tryAcquireNanos(int arg, long nanosTimeout);

    boolean tryAcquireSharedNanos(int arg, long nanosTimeout);

    void extend();

    void setExclusiveOwnerThread(Thread thread);

    Thread getExclusiveOwnerThread();

    String getUuid();
}
