package io.github.yangziwen.logreplay.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {

	@Primary
	@Bean("dataSource")
	@ConfigurationProperties("hiraki.datasource")
	public HikariDataSource dataSource() {
		return (HikariDataSource) DataSourceBuilder
				.create()
				.type(HikariDataSource.class)
				.build();
	}

	@Bean
	public NamedParameterJdbcTemplate jdbcTemplate(@Autowired DataSource dataSource) {
		return new NamedParameterJdbcTemplate(dataSource);
	}

}
