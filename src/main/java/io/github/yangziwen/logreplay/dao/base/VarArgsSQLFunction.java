package io.github.yangziwen.logreplay.dao.base;

import java.util.List;

public class VarArgsSQLFunction {
	
	private final String begin;
	private final String sep;
	private final String end;
	
	public VarArgsSQLFunction(String begin, String sep, String end) {
		this.begin = begin;
		this.sep = sep;
		this.end = end;
	}
	
	public String render(List<?> arguments) {
		StringBuilder buf = new StringBuilder().append(begin);
		for (int i = 0; i < arguments.size(); i++) {
			buf.append(transformArgument((String) arguments.get(i)));
			if (i < arguments.size() - 1) {
				buf.append(sep);
			}
		}
		return buf.append(end).toString();
	}
	
	protected String transformArgument(String argument) {
		return argument;
	}

}
