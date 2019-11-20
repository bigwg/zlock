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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * RedisSource 测试类
 *
 * @author zhaozhiwei
 * @date 2019/9/19 3:03 下午
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RedisSourceTest {

    private Source source;

    private String lockName = "zhaozhiweiTryAcquireLock";

    @BeforeAll
    public void beforeAll(){
        source = new RedisSource("10.12.0.8", 6379, "MBkMl4cssBcbet1W");
    }

    @Test
    public void tryAcquireTest() throws InterruptedException {
        boolean result = source.tryAcquire(lockName, 1);
        Assertions.assertTrue(result);
        Thread.sleep(20000);
        tryReleaseTest();
    }

    @Test
    public void tryReleaseTest(){
        boolean release = source.release(lockName, 1);
        Assertions.assertTrue(release);
    }
}
