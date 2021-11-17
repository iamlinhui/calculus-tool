package cn.promptness.calculus.config;

import cn.promptness.calculus.data.Constant;
import cn.promptness.httpclient.HttpClientAutoConfiguration;
import cn.promptness.httpclient.HttpClientProperties;
import cn.promptness.httpclient.HttpClientUtil;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class HttpClientConfig {

    @Bean
    public HttpClientUtil httpClientUtil() {
        HttpClientProperties properties = new HttpClientProperties();
        properties.setAgent(Constant.USER_AGENT);

        HttpClientAutoConfiguration httpClientAutoConfiguration = new HttpClientAutoConfiguration(properties);
        CloseableHttpClient closeableHttpClient = httpClientAutoConfiguration.httpClient();

        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(properties.getConnectTimeOut())
                .setSocketTimeout(properties.getSocketTimeOut())
                .setConnectionRequestTimeout(properties.getConnectionRequestTimeout())
                .setCookieSpec(properties.getCookieSpecs()).setExpectContinueEnabled(properties.getExpectContinueEnabled())
                .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                .setProxyPreferredAuthSchemes(Collections.singletonList(AuthSchemes.BASIC))
                .setRedirectsEnabled(false).build();

        return new HttpClientUtil(closeableHttpClient, requestConfig);
    }
}
