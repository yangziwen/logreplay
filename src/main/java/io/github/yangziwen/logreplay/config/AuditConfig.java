package io.github.yangziwen.logreplay.config;

import java.util.Arrays;

import javax.sql.DataSource;

import org.audit4j.core.handler.ConsoleAuditHandler;
import org.audit4j.core.layout.CustomizableLayout;
import org.audit4j.handler.db.DatabaseAuditHandler;
import org.audit4j.integration.spring.AuditAspect;
import org.audit4j.integration.spring.SpringAudit4jConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import io.github.yangziwen.logreplay.audit.AuditMetaData;

/**
 * 审计相关的配置
 * 基于@EnableAudit注解控制是否生效
 *
 * @author yangziwen
 */
public class AuditConfig {

	private CustomizableLayout layout() {
		CustomizableLayout layout = new CustomizableLayout();
		layout.setDateFormat("[yyyy-MM-dd HH:mm:ss.SSS]");
		layout.setTemplate("${eventDate} AuditLog |${actor}|${action}|${origin}|{${foreach fields field ,}${field.name}:${field.value}${end}}");
		return layout;
	}

	private ConsoleAuditHandler consoleAuditHandler() {
		return new ConsoleAuditHandler();
	}

	private DatabaseAuditHandler databaseAuditHandler(DataSource dataSource) {
		DatabaseAuditHandler handler = new DatabaseAuditHandler();
		handler.setEmbedded("false");
		handler.setDataSource(dataSource);
		return handler;
	}

	@Bean
	public SpringAudit4jConfig audit4jConfig(@Autowired DataSource dataSource) {
		SpringAudit4jConfig config = new SpringAudit4jConfig();
		config.setMetaData(new AuditMetaData());
		config.setLayout(layout());
		config.setCommands("-objectSerializer=io.github.yangziwen.logreplay.audit.CustomObjectToJsonSerializer");
		config.setHandlers(Arrays.asList(consoleAuditHandler(), databaseAuditHandler(dataSource)));
		return config;
	}

	@Bean
	public AuditAspect auditAspect() {
		return new AuditAspect();
	}

}
