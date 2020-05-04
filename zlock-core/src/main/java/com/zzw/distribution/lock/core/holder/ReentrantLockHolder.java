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
package com.zzw.distribution.lock.core.holder;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 可重入锁指示器
 *
 * @author zhaozhiwei
 * @since 2020/5/3
 */
public class ReentrantLockHolder {

    private final AtomicInteger numLocks = new AtomicInteger(1);

    public void incrLock() {
        numLocks.incrementAndGet();
    }

    public int decrLock() {
        return numLocks.decrementAndGet();
    }

    public static boolean reentrant(ThreadLocal<ReentrantLockHolder> locks) {
        ReentrantLockHolder local = locks.get();
        if (local != null) {
            local.incrLock();
            return true;
        }
        return false;
    }
}
