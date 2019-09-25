package com.zzw.zlock.spring.boot.autoconfigure;

import com.zzw.distribution.lock.core.DefaultDistributionLock;
import com.zzw.distribution.lock.core.DistributedLock;
import com.zzw.distribution.lock.core.source.EtcdSource;
import com.zzw.distribution.lock.core.source.RedisSource;
import com.zzw.distribution.lock.core.source.Source;
import com.zzw.distribution.lock.core.source.ZookeeperSource;
import org.apache.curator.CuratorZookeeperClient;
import org.apache.http.HttpClientConnection;
import org.apache.http.client.HttpClient;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPool;

import static com.zzw.zlock.spring.boot.util.ZlockUtils.ZLOCK_PREFIX;

/**
 * zlock auto configuration
 *
 * @author zhaozhiwei
 * @date 2019/9/25 1:12 上午
 */
@Configuration
@ConditionalOnProperty(prefix = ZLOCK_PREFIX, name = "enabled", value = "true", matchIfMissing = true)
@EnableConfigurationProperties(ZlockConfigurationProperties.class)
public class ZlockAutoConfiguration {

    @Bean
    @Primary
    @ConditionalOnProperty(prefix = ZLOCK_PREFIX, name = "redis")
    @ConditionalOnBean(ZlockConfigurationProperties.class)
    @ConditionalOnClass({JedisCommands.class, JedisPool.class})
    public Source redisSource(ZlockConfigurationProperties properties) {
        RedisConfig redisConfig = properties.getRedis();
        return new RedisSource(redisConfig.getHost(), redisConfig.getPort(), redisConfig.getPassword());
    }

    @Bean
    @ConditionalOnProperty(prefix = ZLOCK_PREFIX, name = "zookeeper")
    @ConditionalOnBean(ZlockConfigurationProperties.class)
    @ConditionalOnMissingBean(Source.class)
    @ConditionalOnClass({ZooKeeper.class, CuratorZookeeperClient.class})
    public Source zookeeperSource(ZlockConfigurationProperties properties) {
        return new ZookeeperSource();
    }

    @Bean
    @ConditionalOnProperty(prefix = ZLOCK_PREFIX, name = "etcd")
    @ConditionalOnBean(ZlockConfigurationProperties.class)
    @ConditionalOnMissingBean(Source.class)
    @ConditionalOnClass({HttpClient.class, HttpClientConnection.class})
    public Source etcdSource(ZlockConfigurationProperties properties) {
        return new EtcdSource();
    }

    @Bean
    @ConditionalOnBean(Source.class)
    public DistributedLock distributedLock(Source source) {
        return new DefaultDistributionLock(source);
    }

}
