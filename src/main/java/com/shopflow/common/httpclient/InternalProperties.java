package com.shopflow.common.httpclient;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.internal")
public class InternalProperties {

    private String baseUrl;
    private boolean enabled = true;
    private long refreshMs = 300_000L;
}
