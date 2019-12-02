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
