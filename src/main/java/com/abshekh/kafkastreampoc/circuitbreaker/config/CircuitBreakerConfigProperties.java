package com.abshekh.kafkastreampoc.circuitbreaker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties("circuit-breaker")
public class CircuitBreakerConfigProperties {
    private Map<String, List<String>> instances;
}
