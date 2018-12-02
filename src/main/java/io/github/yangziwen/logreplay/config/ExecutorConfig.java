package io.github.yangziwen.logreplay.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.google.common.eventbus.AsyncEventBus;

@Configuration
@AutoConfigureBefore(ShiroConfig.class)
public class ExecutorConfig {

    @Bean("executor")
    public ThreadPoolTaskExecutor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(150);
        executor.setQueueCapacity(1000);
        executor.setKeepAliveSeconds(30);
        return executor;
    }

    @Bean("asyncEventBus")
    public AsyncEventBus eventBus(@Autowired ThreadPoolTaskExecutor executor) {
        return new AsyncEventBus(executor);
    }

}
