package io.github.yangziwen.logreplay.logprocess.log;

import java.util.Collections;
import java.util.Map;

public class UrlInfo {

	private String uri;
	
	private String queryString;
	
	private Map<String, String> params;
	
	private UrlInfo(String uri, String queryString, Map<String, String> params) {
		this.uri = uri;
		this.queryString = queryString;
		this.params = params;
	}
	
	public String getUri() {
		return uri;
	}

	public String getQueryString() {
		return queryString;
	}

	public Map<String, String> getParams() {
		return params;
	}
	
	public String getParam(String key) {
		return params.get(key);
	}
	
	@Override
	public String toString() {
		return new StringBuilder("UrlInfo [")
			.append("uri=").append(uri)
			.append(", ")
			.append("queryStringLength=").append(queryString != null? queryString.length(): 0)
			.append(", ")
			.append("paramsSize=").append(params.size())
			.append("]")
			.toString();
	}

	public static class Builder {
		String uri;
		String queryString;
		Map<String, String> params = Collections.emptyMap();
		
		public Builder uri(String uri) {
			this.uri = uri;
			return this;
		}
		
		public Builder queryString(String queryString) {
			this.queryString = queryString;
			return this;
		}
		
		public Builder params(Map<String, String> params) {
			this.params = params;
			return this;
		}
		
		public UrlInfo build() {
			return new UrlInfo(uri, queryString, params);
		}
		
	}
}
