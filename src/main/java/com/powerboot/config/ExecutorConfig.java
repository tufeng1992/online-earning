package com.powerboot.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutorConfig {

    @Bean("commonExecutor")
    public ExecutorService commonExecutor() {
        return new ThreadPoolExecutor(5, 20, 60, TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

    }

}
