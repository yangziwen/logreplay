package com.sogou.map.logreplay.util;

import javax.servlet.http.HttpServletRequest;

public class IPUtil {

	private IPUtil() {}
	
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || " unknown ".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || " unknown ".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || " unknown ".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip != null) {
			String[] ips = ip.split(",");
			if (ips.length > 1)
				ip = ips[0];
		}
		return ip;
	}
}
