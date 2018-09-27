package io.github.yangziwen.logreplay.util;

import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.jrobin.data.DataProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class MonitorUtil {

	private static final Logger logger = LoggerFactory.getLogger(MonitorUtil.class);

	private MonitorUtil() {}

	/**
	 * 获取与name相对应的统计数据
	 * @param application
	 * @param name
	 * @param startTime 开始时间(毫秒)
	 * @param endTime	结束时间(毫秒)
	 * @param step		取样间隔(秒)
	 */
	public static List<Data<Long, Double>> getDataList(
			String application, String name, long startTime, long endTime, long step, ConsolFunc func) {
		File rrdFile = getStorageRrdFile(application, name);
		if(!rrdFile.exists()) {
			return Collections.emptyList();
		}
		try {
			String dsName = name;
			if(dsName.length() > 20) {	// 貌似是melody中的机制
				dsName = dsName.substring(0, 20);
			}
			DataProcessor processor = new DataProcessor(startTime / 1000, endTime / 1000);
			processor.addDatasource("data", rrdFile.getAbsolutePath(), dsName, func.name());
			processor.setStep(step);
			// poolUsed设为true，会使用read_write模式，
			// 只有这样，打开rrd文件和调用fileChannel.map方法时的读写模式才会前后一致
			processor.setPoolUsed(true);
			processor.processData();


			double[] values = processor.getValues("data");
			long[] timestamps = processor.getTimestamps();
			assert values.length == timestamps.length;

			List<Data<Long, Double>> dataList = Lists.newArrayList();
			for(int i = 0, l = timestamps.length; i < l; i++) {
				dataList.add(new Data<Long, Double>()
					.key(timestamps[i] * 1000)
					.value(Double.isNaN(values[i])? 0D: values[i])
				);
			}
			return dataList;
		} catch (Exception e) {
			logger.error("error happens when get monitor data[{}] for application[{}]", name, application, e);
			return Collections.emptyList();
		}

	}

	/**
	 * 获取监控指标所对应的rrd文件
	 */
	public static File getStorageRrdFile(String application, String name) {
		return new File(getStorageDirectory(application), name + ".rrd");
	}

	/**
	 * 获取存放监控指标的目录
	 */
	public static File getStorageDirectory(String application) {
		String directoryPath = FilenameUtils.concat(SystemUtils.JAVA_IO_TMPDIR, "javamelody");
		return new File(FilenameUtils.concat(directoryPath, application));
	}

	/**
	 * 获取应用名称
	 */
	public static String getCurrentApplication(ServletContext servletContext) {
		String contextPath = getContextPath(servletContext);
		return StringUtils.defaultIfBlank(contextPath, "/").substring(1) + '_' + getHostName();
	}

	private static String getContextPath(ServletContext context) {
		// cette m茅thode retourne le contextPath de la webapp
		// en utilisant ServletContext.getContextPath si servlet api 2.5
		// ou en se d茅brouillant sinon
		// (on n'a pas encore pour l'instant de request pour appeler HttpServletRequest.getContextPath)
		if (context.getMajorVersion() == 2 && context.getMinorVersion() >= 5
				|| context.getMajorVersion() > 2) {
			// api servlet 2.5 (Java EE 5) minimum pour appeler ServletContext.getContextPath
			return context.getContextPath();
		}
		URL webXmlUrl;
		try {
			webXmlUrl = context.getResource("/WEB-INF/web.xml");
		} catch (final MalformedURLException e) {
			throw new IllegalStateException(e);
		}
		String contextPath = webXmlUrl.toExternalForm();
		contextPath = contextPath.substring(0, contextPath.indexOf("/WEB-INF/web.xml"));
		final int indexOfWar = contextPath.indexOf(".war");
		if (indexOfWar > 0) {
			contextPath = contextPath.substring(0, indexOfWar);
		}
		// tomcat peut renvoyer une url commen莽ant pas "jndi:/localhost"
		// (v5.5.28, webapp dans un r茅pertoire)
		if (contextPath.startsWith("jndi:/localhost")) {
			contextPath = contextPath.substring("jndi:/localhost".length());
		}
		final int lastIndexOfSlash = contextPath.lastIndexOf('/');
		if (lastIndexOfSlash != -1) {
			contextPath = contextPath.substring(lastIndexOfSlash);
		}
		return contextPath;
	}

	public static String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (final UnknownHostException ex) {
			return "localhost";
		}
	}

	public static class Data<K, V> {

		private K key;
		private V value;

		public K getKey() {
			return key;
		}
		public Data<K, V> key(K key) {
			this.key = key;
			return this;
		}
		public V getValue() {
			return value;
		}
		public Data<K, V> value(V value) {
			this.value = value;
			return this;
		}

		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		}
	}

	public static enum ConsolFunc {
		MIN, MAX, AVERAGE, LAST;
	}
}
