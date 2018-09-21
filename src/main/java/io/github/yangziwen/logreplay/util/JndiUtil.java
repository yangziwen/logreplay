package io.github.yangziwen.logreplay.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JndiUtil {

	private static final Logger logger = LoggerFactory.getLogger(JndiUtil.class);

	private static final InitialContext context = initContext();

	private JndiUtil() {}

	private static InitialContext initContext() {
		try {
			return new InitialContext();
		} catch (NamingException e) {
			throw new IllegalStateException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T lookup(String name) {
		try {
			return (T) context.lookup(name);
		} catch (Exception e) {
			logger.error("failed to lookup {} from jndi", name, e);
			return null;
		}
	}
}
