package com.zzw.zlock.spring.boot.autoconfigure;

/**
 * redis config
 *
 * @author zhaozhiwei
 * @date 2019/9/25 12:49 下午
 */
public class RedisConfig {
    private String host;
    private Integer port;
    private String password;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
