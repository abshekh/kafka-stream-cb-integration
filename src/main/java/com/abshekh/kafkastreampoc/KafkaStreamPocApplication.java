package com.abshekh.kafkastreampoc;

import io.github.resilience4j.common.retry.configuration.RetryConfigCustomizer;
import io.github.resilience4j.core.IntervalFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

@SpringBootApplication
@Slf4j
public class KafkaStreamPocApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaStreamPocApplication.class, args);
    }
}