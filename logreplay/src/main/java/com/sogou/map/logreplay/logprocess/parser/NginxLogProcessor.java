package com.sogou.map.logreplay.logprocess.parser;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.sogou.map.logreplay.logprocess.log.NginxLog;

public class NginxLogProcessor {
	
	private ParserChain parserChain = new ParserChain()
		.addParser(new IpParser())
		.addParser(new TimestampParser())
		.addParser(new RequestMethodParser())
		.addParser(new UrlParser())
		.addParser(new HttpProtocolParser())
	;
	
	public NginxLog process(String log) {
		if(!parserChain.parse(log)) {
			return null;
		}
		return new NginxLog.Builder()
		.ip(parserChain.getParser(IpParser.class.getSimpleName()).getContent())
		.timestamp(parserChain.getParser(TimestampParser.class.getSimpleName()).getContent())
		.requestMethod(parserChain.getParser(RequestMethodParser.class.getSimpleName()).getContent())
		.url(parserChain.getParser(UrlParser.class.getSimpleName()).getContent())
		.httpProtocol(parserChain.getParser(HttpProtocolParser.class.getSimpleName()).getContent())
		.build();
	}
	
	static class IpParser extends Parser {
		private static final Pattern IP_PATTERN = Pattern.compile("^\\d{1,3}(?:\\.\\d{1,3}){3}$");
		@Override
		public boolean parse(String log, int offset) {
			beginIndex = offset;
			endIndex = log.indexOf(" ");
			if(endIndex <= beginIndex) {
				return false;
			}
			content = log.substring(beginIndex, endIndex);
			return IP_PATTERN.matcher(content).matches();
		}
		
	}
	
	static class TimestampParser extends Parser {
		@Override
		public boolean parse(String log, int offset) {
			beginIndex = log.indexOf("[", offset) + 1;
			endIndex = log.indexOf("]", beginIndex);
			if(endIndex <= beginIndex) {
				return false;
			}
			content = log.substring(beginIndex, endIndex);
			return true;
		}
	}
	
	static class RequestMethodParser extends Parser {
		private static final List<String> METHOD_LIST = Arrays.asList(new String[]{
			"GET", "POST", "PUT", "DELETE", "HEAD"
		});
		@Override
		public boolean parse(String log, int offset) {
			beginIndex = log.indexOf("\"", offset) + 1;
			endIndex = log.indexOf(" ", beginIndex);
			if(endIndex <= beginIndex) {
				return false;
			}
			return METHOD_LIST.contains(content.toUpperCase());
		}
	}
	
	static class UrlParser extends Parser {
		@Override
		public boolean parse(String log, int offset) {
			beginIndex = log.indexOf("/", offset);
			endIndex = log.indexOf(" HTTP/1.1\"", beginIndex);
			if(endIndex == -1) {
				endIndex = log.indexOf(" HTTP/1.0\"", beginIndex);
			}
			if(endIndex == -1) {
				endIndex = log.indexOf(" HTTP/0.9\"", beginIndex);
			}
			if(endIndex <= beginIndex) {
				return false;
			}
			content = log.substring(beginIndex, endIndex);
			return true;
		}
	}
	
	static class HttpProtocolParser extends Parser {
		private static final List<String> PROTOCOL_LIST = Arrays.asList(new String[]{
				"HTTP/1.1", "HTTP/1.0", "HTTP/0.9"
		});
		@Override
		public boolean parse(String log, int offset) {
			beginIndex = log.indexOf(" HTTP/", offset);
			endIndex = log.indexOf("\"", beginIndex);
			if(endIndex <= beginIndex) {
				return false;
			}
			content = log.substring(beginIndex, endIndex);
			return PROTOCOL_LIST.contains(content);
		}
		
	}
	

}
