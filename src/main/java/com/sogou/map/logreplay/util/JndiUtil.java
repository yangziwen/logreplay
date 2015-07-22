package com.sogou.map.logreplay.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JndiUtil {

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
			e.printStackTrace();
			return null;
		}
	}
}
