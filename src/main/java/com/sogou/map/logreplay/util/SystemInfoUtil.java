package com.sogou.map.logreplay.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.poi.util.IOUtils;

import net.bull.javamelody.Parameter;

public class SystemInfoUtil {
	
	private static final String[] OS = { "linux", "windows", "mac", "solaris", "hp", "ibm"};
	
	private static final String[] APPLICATION_SERVERS = {
			"tomcat", "glassfish", "jonas", "jetty",
			"oracle", "bea", "ibm", "jboss", "wildfly"
	};
	
	private static final Date START_TIME = new Date();
	
	private static final String JAVA_VERSION = System.getProperty("java.runtime.name") 
			+ ", " 
			+ System.getProperty("java.runtime.version");
	
	private static final String JVM_VERSION = System.getProperty("java.vm.name") 
			+ ", "
			+ System.getProperty("java.vm.version") 
			+ ", " 
			+ System.getProperty("java.vm.info");
	
	private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
	
	private static final String JVM_ARGUMENTS = doGetJvmArguments();
	
	private static final String OS_INFO = doGetOsInfo();
	
	private static final String PID = doGetPID();
	
	private SystemInfoUtil() {}
	
	/**
	 * 获取当前应用启动时间
	 */
	public static String getStartTime() {
		return DateFormatUtils.format(START_TIME, "yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 获取java版本信息
	 */
	public static String getJavaVersion() {
		return JAVA_VERSION;
	}
	
	/**
	 * 获取JVM版本信息
	 */
	public static String getJvmVersion() {
		return JVM_VERSION;
	}
	
	/**
	 * 获取cpu内核数
	 */
	public static int getAvailableProcessors() {
		return AVAILABLE_PROCESSORS;
	}
	
	/**
	 * 获取jvm参数
	 */
	public static String getJvmArguments() {
		return JVM_ARGUMENTS;
	}
	
	private static String doGetJvmArguments() {
		final StringBuilder jvmArgs = new StringBuilder();
		for (final String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
			jvmArgs.append(jvmArg).append('\n');
		}
		if (jvmArgs.length() > 0) {
			jvmArgs.deleteCharAt(jvmArgs.length() - 1);
		}
		return jvmArgs.toString();
	}
	
	/**
	 * 获取当前操作系统信息
	 */
	public static String getOsInfo() {
		return OS_INFO;
	}
	
	private static String doGetOsInfo() {
		final String name = System.getProperty("os.name");
		final String version = System.getProperty("os.version");
		final String patchLevel = System.getProperty("sun.os.patch.level");
		final String arch = System.getProperty("os.arch");
		final String bits = System.getProperty("sun.arch.data.model");

		final StringBuilder sb = new StringBuilder();
		sb.append(name).append(", ");
		if (!name.toLowerCase(Locale.ENGLISH).contains("windows")) {
			// version is "6.1" and useless for os.name "Windows 7",
			// and can be "2.6.32-358.23.2.el6.x86_64" for os.name "Linux"
			sb.append(version).append(' ');
		}
		if (StringUtils.isNotBlank(patchLevel) && !"unknown".equals(patchLevel)) {
			// patchLevel is "unknown" and useless on Linux,
			// and can be "Service Pack 1" on Windows
			sb.append(patchLevel).append(", ");
		}
		sb.append(arch).append('/').append(bits);
		return sb.toString();
	}
	
	/**
	 * 获取当前操作系统的图标
	 */
	public static String getOsIcon() {
		final String tmp = OS_INFO.toLowerCase(Locale.ENGLISH);
		for (final String os : OS) {
			if (tmp.contains(os)) {
				return os + ".png";
			}
		}
		return null;
	}
	
	/**
	 * 获取当前web服务器图标
	 */
	public static String getServerIcon(String serverInfo) {
		final String tmp = serverInfo.toLowerCase(Locale.ENGLISH);
		for (final String applicationServer : APPLICATION_SERVERS) {
			if (tmp.contains(applicationServer)) {
				return applicationServer + ".png";
			}
		}
		return null;
	}
	
	/**
	 * 获取当前应用的进程号
	 */
	public static String getPID() {
		return PID;
	}
	
	private static String doGetPID() {
		String pid = System.getProperty("pid");
		if (pid == null) {
			// first, reliable with sun jdk (http://golesny.de/wiki/code:javahowtogetpid)
			final RuntimeMXBean rtb = ManagementFactory.getRuntimeMXBean();
			final String processName = rtb.getName();
			/* tested on: */
			/* - windows xp sp 2, java 1.5.0_13 */
			/* - mac os x 10.4.10, java 1.5.0 */
			/* - debian linux, java 1.5.0_13 */
			/* all return pid@host, e.g 2204@antonius */

			if (processName.indexOf('@') != -1) {
				pid = processName.substring(0, processName.indexOf('@'));
			} else {
				pid = getPIDFromOS();
			}
			System.setProperty("pid", pid);
		}
		return pid;
	}
	
	private static String getPIDFromOS() {
		String pid;
		// following is not always reliable as is (for example, see issue 3 on solaris 10
		// or http://blog.igorminar.com/2007/03/how-java-application-can-discover-its.html)
		// Author: Santhosh Kumar T, http://code.google.com/p/jlibs/, licence LGPL
		// Author getpids.exe: Daniel Scheibli, http://www.scheibli.com/projects/getpids/index.html, licence GPL
		final String[] cmd;
		File tempFile = null;
		Process process = null;
		try {
			try {
				if (!System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("windows")) {
					cmd = new String[] { "/bin/sh", "-c", "echo $$ $PPID" };
				} else {
					// getpids.exe is taken from http://www.scheibli.com/projects/getpids/index.html (GPL)
					tempFile = File.createTempFile("getpids", ".exe");

					// extract the embedded getpids.exe file from the jar and save it to above file
					pump(Parameter.class.getResourceAsStream("resource/getpids.exe"),
							new FileOutputStream(tempFile), true, true);
					cmd = new String[] { tempFile.getAbsolutePath() };
				}
				process = Runtime.getRuntime().exec(cmd);
				final ByteArrayOutputStream bout = new ByteArrayOutputStream();
				pump(process.getInputStream(), bout, false, true);

				final StringTokenizer stok = new StringTokenizer(bout.toString());
				stok.nextToken(); // this is pid of the process we spanned
				pid = stok.nextToken();

				// waitFor nécessaire sous windows server 2003
				// (sinon le fichier temporaire getpidsxxx.exe n'est pas effacé)
				process.waitFor();
			} finally {
				if (process != null) {
					// évitons http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6462165
					process.getInputStream().close();
					process.getOutputStream().close();
					process.getErrorStream().close();
					process.destroy();
				}
				if (tempFile != null && !tempFile.delete()) {
					tempFile.deleteOnExit();
				}
			}
		} catch (final InterruptedException e) {
			pid = e.toString();
		} catch (final IOException e) {
			pid = e.toString();
		}
		return pid;
	}
	
	private static void pump(InputStream is, OutputStream os, boolean closeIn, boolean closeOut)
			throws IOException {
		try {
			IOUtils.copy(is, os);
		} finally {
			try {
				if (closeIn) {
					is.close();
				}
			} finally {
				if (closeOut) {
					os.close();
				}
			}
		}
	}
	
}
