package io.github.yangziwen.logreplay.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {

	@Autowired
	private Environment env;

	@Primary
	@Bean("dataSource")
	public DataSource dataSource() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(env.getProperty("spring.datasource.url"));
		config.setUsername(env.getProperty("spring.datasource.username"));
		config.setPassword(env.getProperty("spring.datasource.password"));
		config.setMinimumIdle(10);
		config.setMaximumPoolSize(100);
		return new HikariDataSource(config);
	}

	@Bean
	public NamedParameterJdbcTemplate jdbcTemplate(@Autowired DataSource dataSource) {
		return new NamedParameterJdbcTemplate(dataSource);
	}

}
