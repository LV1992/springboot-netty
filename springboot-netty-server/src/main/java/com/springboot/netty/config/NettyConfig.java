package com.springboot.netty.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "netty")
public class NettyConfig {
    private int port;
    private int maxThreads;
    private int maxFrameLength;
}
