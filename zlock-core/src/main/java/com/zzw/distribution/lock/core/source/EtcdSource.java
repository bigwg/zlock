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

import com.zzw.distribution.lock.core.util.PoolHttpClient;

/**
 * Etcd 服务提供的锁
 *
 * @author zhaozhiwei
 * @date 2019/5/29 15:08
 */
public class EtcdSource implements Source {

    /**
     * 池化管理器
     */
    private String url;
    private PoolHttpClient poolHttpClient;

    public EtcdSource() {
        this.url = "http://127.0.0.1:2379";
        try {
            this.poolHttpClient = PoolHttpClient.getPoolHttpClient();
        } catch (Exception e) {
            throw new RuntimeException("init EtcdSource error, error message: " + e.getMessage());
        }

    }

    public EtcdSource(String url) {
        this();
        this.url = url;
    }

    @Override
    public void acquire(String lockName, int arg) {

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
        return false;
    }

    @Override
    public boolean releaseShared(String lockName, int arg) {
        return false;
    }

    @Override
    public boolean tryAcquire(String lockName, int arg) {
        return false;
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

    }
}
