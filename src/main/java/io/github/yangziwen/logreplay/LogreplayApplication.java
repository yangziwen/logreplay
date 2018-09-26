package io.github.yangziwen.logreplay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import com.google.common.eventbus.AsyncEventBus;

@ServletComponentScan
@EnableTransactionManagement
@EnableWebSocketMessageBroker
@SpringBootApplication
public class LogreplayApplication {

	@Bean("executor")
	public ThreadPoolTaskExecutor executor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(20);
		executor.setMaxPoolSize(150);
		executor.setQueueCapacity(1000);
		executor.setKeepAliveSeconds(30);
		return executor;
	}

	@Bean("eventBus")
	public AsyncEventBus eventBus(@Autowired ThreadPoolTaskExecutor executor) {
		return new AsyncEventBus(executor);
	}

	public static void main(String[] args) {
		System.setProperty("tomcat.util.http.parser.HttpParser.requestTargetAllow","|{}");
		new SpringApplicationBuilder(LogreplayApplication.class).bannerMode(Mode.OFF).run(args);
	}

}
