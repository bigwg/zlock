package com.zzw.zlock.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.zzw.zlock.spring.boot.util.ZlockUtils.ZLOCK_PREFIX;

/**
 * zlock configuration properties
 *
 * @author zhaozhiwei
 * @date 2019/9/25 1:23 上午
 */
@ConfigurationProperties(ZLOCK_PREFIX)
public class ZlockConfigurationProperties {

}
