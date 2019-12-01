package com.zzw.distribution.lock.core.source;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;

/**
 * {@link ZookeeperSource} 测试类
 *
 * @author zhaozhiwei
 * @date 2019/12/1 10:58 上午
 * @see ZookeeperSource
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ZookeeperSourceTest {

    private Source source;
    private String lockName = "zookeeperTryAcquireLock";
    private String localIp;
    private CuratorFramework client;

    @BeforeAll
    public void beforeAll() {
//        source = new ZookeeperSource("172.16.120.10:2181");
        try {
            this.localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            this.localIp = UUID.randomUUID().toString();
        }
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        this.client = CuratorFrameworkFactory.newClient("172.16.120.10:2181", retryPolicy);
        client.start();
    }

    @Test
    public void zookeeperCreateTest() throws Exception {
        String key = client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath("/zlock/" + lockName + "/" + lockName, localIp.getBytes());
        System.out.println(key);
        Thread.sleep(60000);
    }

    @Test
    public void zookeeperGetTest() throws Exception {
        List<String> values = client.getChildren().forPath("/zlock");
        for (String value : values) {
            System.out.println(value);
        }
    }

    @Test
    public void zookeeperDeleteTest() throws Exception {
        client.delete().forPath("/zlock");
    }
}
