package com.berkaykomur.filesearchbackend.config;

import com.berkaykomur.filesearchbackend.model.FileEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
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


}
