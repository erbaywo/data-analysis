package org.yc.analysis.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "feishu")
@Data
public class FeiShuConfig {

    private String appId;
    private String appSecret;
    private String appToken;
    private String accountTableId;
    private String videoTableId;
    private String baseUrl;
    private String redirectUri;
}
