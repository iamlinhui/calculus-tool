package cn.promptness.calculus.config;

import cn.promptness.calculus.data.Constant;
import cn.promptness.calculus.utils.SnowflakeIdUtil;
import cn.promptness.httpclient.HttpClientProperties;
import cn.promptness.httpclient.HttpClientUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public HttpClientUtil httpClientUtil(HttpClientProperties properties) {
        return new HttpClientUtil(properties);
    }

    @Bean
    public HttpClientProperties httpClientProperties(@Value("${spring.profiles.active}") String activeProfiles) {
        HttpClientProperties properties = new HttpClientProperties();
        properties.setAgent(Constant.USER_AGENT);
        properties.setIpLabel(activeProfiles);
        return properties;
    }

    @Bean
    public SnowflakeIdUtil snowflakeIdUtil() {
        return new SnowflakeIdUtil(1, 1);
    }
}
