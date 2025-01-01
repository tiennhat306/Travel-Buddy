package com.travelbuddy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
public class AsyncExecutorConfig {

    @Bean(name = "applicationTaskExecutor") // Đảm bảo tên bean khớp với @Qualifier
    public AsyncTaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("application-executor-");
    }
}
