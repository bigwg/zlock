package com.zzw.zlock.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import static com.zzw.zlock.spring.boot.util.ZlockUtils.ZLOCK_PREFIX;

/**
 * zlock configuration properties
 *
 * @author zhaozhiwei
 * @date 2019/9/25 1:23 上午
 */
@ConfigurationProperties(ZLOCK_PREFIX)
public class ZlockConfigurationProperties {

    private Boolean enable = Boolean.TRUE;

    @NestedConfigurationProperty
    private RedisConfig redis = new RedisConfig();

    public RedisConfig getRedis() {
        return redis;
    }

    public void setRedis(RedisConfig redis) {
        this.redis = redis;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
}
