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
package com.zzw.zlock.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import static com.zzw.zlock.spring.boot.constant.ZlockConstant.ZLOCK_PREFIX;


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
