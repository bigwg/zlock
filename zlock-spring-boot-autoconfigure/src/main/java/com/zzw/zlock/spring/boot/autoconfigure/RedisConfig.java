package com.zzw.zlock.spring.boot.autoconfigure;

/**
 * redis config
 *
 * @author zhaozhiwei
 * @date 2019/9/25 12:49 下午
 */
public class RedisConfig {
    private String host;
    private String port;
    private String password;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
