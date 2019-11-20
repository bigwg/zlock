package com.zzw.distribution.lock.core.util;

import com.zzw.distribution.lock.core.LockExecutors;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * 池化HttpClient工具类
 *
 * @author zhaozhiwei
 * @date 2019/11/6 3:00 下午
 */
public class PoolHttpClient {

    private CloseableHttpClient httpClient;
    private static PoolHttpClient poolHttpClient;

    public PoolHttpClient() {
        SSLContextBuilder builder = new SSLContextBuilder();
        SSLConnectionSocketFactory sslConnectionSocketFactory;
        try {
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            sslConnectionSocketFactory = new SSLConnectionSocketFactory(builder.build());
        } catch (Exception e) {
            throw new RuntimeException("init PoolHttpClient error");
        }
        // 配置同时支持 HTTP 和 HTPPS
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslConnectionSocketFactory).build();
        // 初始化连接管理器
        PoolingHttpClientConnectionManager poolConnManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        // 同时最多连接数
        poolConnManager.setMaxTotal(640);
        // 设置最大路由
        poolConnManager.setDefaultMaxPerRoute(320);
        // 初始化httpClient
        RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(5000).setSocketTimeout(5000).build();
        httpClient = HttpClients.custom()
                // 设置连接池管理
                .setConnectionManager(poolConnManager)
                .setDefaultRequestConfig(config)
                // 设置重试次数
                .setRetryHandler(new DefaultHttpRequestRetryHandler(2, false)).build();
    }

    public static PoolHttpClient getPoolHttpClient() {
        if (poolHttpClient != null) {
            return poolHttpClient;
        } else {
            synchronized (LockExecutors.class) {
                if (poolHttpClient == null) {
                    poolHttpClient = new PoolHttpClient();
                }
                return poolHttpClient;
            }
        }
    }

    public String get(String url) {
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            String result = EntityUtils.toString(response.getEntity());
            int code = response.getStatusLine().getStatusCode();
            if (code == HttpStatus.SC_OK) {
                return result;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String post(String uri, String params, Header... heads) {
        HttpPost httpPost = new HttpPost(uri);
        CloseableHttpResponse response = null;
        try {
            StringEntity paramEntity = new StringEntity(params);
            paramEntity.setContentEncoding("UTF-8");
            paramEntity.setContentType("application/json");
            httpPost.setEntity(paramEntity);
            if (heads != null) {
                httpPost.setHeaders(heads);
            }
            response = httpClient.execute(httpPost);
            int code = response.getStatusLine().getStatusCode();
            String result = EntityUtils.toString(response.getEntity());
            if (code == HttpStatus.SC_OK) {
                return result;
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String delete(String uri) {
        HttpDelete httpDelete = new HttpDelete(uri);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpDelete);
            int code = response.getStatusLine().getStatusCode();
            String result = EntityUtils.toString(response.getEntity());
            if (code == HttpStatus.SC_OK) {
                return result;
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String put(String uri, String params, Header... heads) {
        HttpPut httpPut = new HttpPut(uri);
        CloseableHttpResponse response = null;
        try {
            StringEntity paramEntity = new StringEntity(params);
            paramEntity.setContentEncoding("UTF-8");
            paramEntity.setContentType("application/json");
            httpPut.setEntity(paramEntity);
            if (heads != null) {
                httpPut.setHeaders(heads);
            }
            response = httpClient.execute(httpPut);
            int code = response.getStatusLine().getStatusCode();
            String result = EntityUtils.toString(response.getEntity());
            if (code == HttpStatus.SC_OK) {
                return result;
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
