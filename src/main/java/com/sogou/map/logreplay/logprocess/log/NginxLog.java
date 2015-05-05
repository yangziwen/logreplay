package com.sogou.map.logreplay.logprocess.log;

public class NginxLog {
	
	private String ip;
	private String timestamp;
	private String requestMethod;
	private String url;
	private String httpProtocol;
	
	private NginxLog(
			String ip, 
			String timestamp, 
			String requestMethod, 
			String url, 
			String httpProtocol) {
		this.ip = ip;
		this.timestamp = timestamp;
		this.requestMethod = requestMethod;
		this.url = url;
		this.httpProtocol = httpProtocol;
	}
	
	public String getIp() {
		return ip;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public String getUrl() {
		return url;
	}

	public String getHttpProtocol() {
		return httpProtocol;
	}
	
	@Override
	public String toString() {
		return new StringBuilder("NginxLog [")
			.append("ip=").append(ip)
			.append(", ")
			.append("timestamp=").append(timestamp)
			.append(", ")
			.append("requestMethod=").append(requestMethod)
			.append(", ")
			.append("urlLength=").append(url != null? url.length(): 0)
			.append(", ")
			.append("httpProtocol=").append(httpProtocol)
			.append("]")
			.toString();
	}

	public static class Builder {
		String ip;
		String timestamp;
		String requestMethod;
		String url;
		String httpProtocol;
		
		public Builder ip(String ip) {
			this.ip = ip;
			return this;
		}
		public Builder timestamp(String timestamp) {
			this.timestamp = timestamp;
			return this;
		}
		public Builder requestMethod(String requestMethod) {
			this.requestMethod = requestMethod;
			return this;
		}
		public Builder url(String url) {
			this.url = url;
			return this;
		}
		public Builder httpProtocol(String httpProtocol) {
			this.httpProtocol = httpProtocol;
			return this;
		}
		public NginxLog build() {
			return new NginxLog(ip, timestamp, requestMethod, url, httpProtocol);
		}
		
	}

}
