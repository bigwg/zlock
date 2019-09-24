package com.zzw.zlock.spring.boot.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static com.zzw.zlock.spring.boot.util.ZlockUtils.ZLOCK_PREFIX;

/**
 * zlock auto configuration
 *
 * @author zhaozhiwei
 * @date 2019/9/25 1:12 上午
 */
@ConditionalOnProperty(prefix = ZLOCK_PREFIX, name = "enabled", matchIfMissing = true)
@Configuration
@EnableConfigurationProperties(ZlockConfigurationProperties.class)
public class ZlockAutoConfiguration {
}
