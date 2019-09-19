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
