package io.github.yangziwen.logreplay.bean.base;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBean {

	public abstract Long getId();

	public abstract void setId(Long id);

	@Override
	public String toString() {
		return toString(ToStringStyle.MULTI_LINE_STYLE);
	}

	public String toString(ToStringStyle style) {
		return ToStringBuilder.reflectionToString(this, style);
	}

	protected Logger logger() {
		return LoggerFactory.getLogger(this.getClass());
	}

}
