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
