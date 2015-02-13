package com.sogou.map.logreplay.logprocess.parser;

import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.commons.collections.MapUtils;


public class ParserChain {

	private LinkedHashMap<String, Parser> parsers = new LinkedHashMap<String, Parser>();
	
	public ParserChain addParser(Parser parser) {
		return addParser(parser.getClass().getSimpleName(), parser);
	}
	
	public ParserChain addParser(String name, Parser parser) {
		parsers.put(name, parser);
		return this;
	}
	
	public Parser removeParser(String name) {
		return parsers.remove(name);
	}
	
	public Parser removeParser(Class<Parser> clazz) {
		Iterator<Parser> iter = parsers.values().iterator();
		Parser parser = null;
		while(iter.hasNext()) {
			parser = iter.next();
			if(clazz.isInstance(parser)){
				iter.remove();
				return parser;
			}
		}
		return null;
	}
	
	public Parser getParser(String name) {
		return parsers.get(name);
	}
	
	public Parser getParser(Class<Parser> clazz) {
		for(Parser parser: parsers.values()) {
			if(clazz.isInstance(parser)) {
				return parser;
			}
		}
		return null;
	}
	
	public Parser getParser(int index) {
		return parsers.values().toArray(new Parser[]{})[index];
	}
	
	public void reset() {
		for(Parser parser: parsers.values()) {
			parser.reset();
		}
	}
	
	public void clear() {
		parsers.clear();
	}
	
	public boolean parse(String log){
		if(MapUtils.isEmpty(parsers)) {
			return false;
		}
		int offset = 0;
		for(Parser parser: parsers.values()){
			if(!parser.parse(log, offset)){
				return false;
			}
			offset = parser.getEndIndex();
		}
		return true;
	}
	
}
