package com.berkaykomur.filesearchbackend.config;

import com.berkaykomur.filesearchbackend.model.FileEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean
    public BlockingQueue<FileEntity> fileQueue() {
        return new LinkedBlockingQueue<>(10000);
    }
    @Bean
    public BlockingQueue<Path> indexQueue() {
        return new LinkedBlockingQueue<>(10000);
    }

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setThreadNamePrefix("workerThread-");
        executor.initialize();
        return executor;
    }
}
