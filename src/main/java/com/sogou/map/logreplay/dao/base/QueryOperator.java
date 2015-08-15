package com.sogou.map.logreplay.dao.base;


import java.util.Arrays;

public enum QueryOperator {

	eq {
		@Override
		public OperationParsedResult buildResult(String fieldName, String originKey, String placeholderPrefix, String placeholderSuffix) {
			return equal_to.buildResult(fieldName, originKey, placeholderPrefix, placeholderSuffix);
		}
	},
	equal_to {
		@Override
		public OperationParsedResult buildResult(String fieldName, String originKey, String placeholderPrefix, String placeholderSuffix) {
			return new OperationParsedResult(fieldName, " = ", wrapPlaceholder(originKey, placeholderPrefix, placeholderSuffix), originKey);
		}
	},
	ne {
		@Override
		public OperationParsedResult buildResult(String fieldName, String originKey, String placeholderPrefix, String placeholderSuffix) {
			return not_equal_to.buildResult(fieldName, originKey, placeholderPrefix, placeholderSuffix);
		}
	},
	not_equal_to {
		@Override
		public OperationParsedResult buildResult(String fieldName, String originKey, String placeholderPrefix, String placeholderSuffix) {
			return new OperationParsedResult(fieldName, " != ", wrapPlaceholder(originKey, placeholderPrefix, placeholderSuffix), originKey);
		}
	},
	gt {
		@Override
		public OperationParsedResult buildResult(String fieldName, String originKey, String placeholderPrefix, String placeholderSuffix) {
			return greater_than.buildResult(fieldName, originKey, placeholderPrefix, placeholderSuffix);
		}
	},
	greater_than {
		@Override
		public OperationParsedResult buildResult(String fieldName, String originKey, String placeholderPrefix, String placeholderSuffix) {
			return new OperationParsedResult(fieldName, " > ", wrapPlaceholder(originKey, placeholderPrefix, placeholderSuffix), originKey);
		}
	},
	ge {
		@Override
		public OperationParsedResult buildResult(String fieldName, String originKey, String placeholderPrefix, String placeholderSuffix) {
			return greater_than_or_equal_to.buildResult(fieldName, originKey, placeholderPrefix, placeholderSuffix);
		}
	},
	greater_than_or_equal_to {
		@Override
		public OperationParsedResult buildResult(String fieldName, String originKey, String placeholderPrefix, String placeholderSuffix) {
			return new OperationParsedResult(fieldName, " >= ", wrapPlaceholder(originKey, placeholderPrefix, placeholderSuffix), originKey);
		}
	},
	lt {
		@Override
		public OperationParsedResult buildResult(String fieldName, String originKey, String placeholderPrefix, String placeholderSuffix) {
			return less_than.buildResult(fieldName, originKey, placeholderPrefix, placeholderSuffix);
		}
	},
	less_than {
		@Override
		public OperationParsedResult buildResult(String fieldName, String originKey, String placeholderPrefix, String placeholderSuffix) {
			return new OperationParsedResult(fieldName, " < ", wrapPlaceholder(originKey, placeholderPrefix, placeholderSuffix), originKey);
		}
	},
	le {
		@Override
		public OperationParsedResult buildResult(String fieldName, String originKey, String placeholderPrefix, String placeholderSuffix) {
			return less_than_or_equal_to.buildResult(fieldName, originKey, placeholderPrefix, placeholderSuffix);
		}
	},
	less_than_or_equal_to {
		@Override
		public OperationParsedResult buildResult(String fieldName, String originKey, String placeholderPrefix, String placeholderSuffix) {
			return new OperationParsedResult(fieldName, " <= ", wrapPlaceholder(originKey, placeholderPrefix, placeholderSuffix), originKey);
		}
	},
	contain {
		@Override
		public OperationParsedResult buildResult(String fieldName, String originKey, String placeholderPrefix, String placeholderSuffix) {
			String placeholder = concatFunction.render(Arrays.asList("'%'", wrapPlaceholder(originKey, placeholderPrefix, placeholderSuffix), "'%'"));
			return new OperationParsedResult(fieldName, " like ", placeholder, originKey);
		}
	},
	not_contain {
		@Override
		public OperationParsedResult buildResult(String fieldName, String originKey, String placeholderPrefix, String placeholderSuffix) {
			String placeholder = concatFunction.render(Arrays.asList("'%'", wrapPlaceholder(originKey, placeholderPrefix, placeholderSuffix), "'%'"));
			return new OperationParsedResult(fieldName, " not like ", placeholder, originKey);
			
		}
	},
	start_with {
		@Override
		public OperationParsedResult buildResult(String fieldName, String originKey, String placeholderPrefix, String placeholderSuffix) {
			String placeholder = concatFunction.render(Arrays.asList(wrapPlaceholder(originKey, placeholderPrefix, placeholderSuffix), "'%'"));
			return new OperationParsedResult(fieldName, " like ", placeholder, originKey);
		}
	},
	not_start_with {
		@Override
		public OperationParsedResult buildResult(String fieldName, String originKey, String placeholderPrefix, String placeholderSuffix) {
			String placeholder = concatFunction.render(Arrays.asList(wrapPlaceholder(originKey, placeholderPrefix, placeholderSuffix), "'%'"));
			return new OperationParsedResult(fieldName, " not like ", placeholder, originKey);
		}
	},
	end_with {
		@Override
		public OperationParsedResult buildResult(String fieldName, String originKey, String placeholderPrefix, String placeholderSuffix) {
			String placeholder = concatFunction.render(Arrays.asList("'%'", wrapPlaceholder(originKey, placeholderPrefix, placeholderSuffix)));
			return new OperationParsedResult(fieldName, " like ", placeholder, originKey);
		}
	},
	not_end_with {
		@Override
		public OperationParsedResult buildResult(String fieldName, String originKey, String placeholderPrefix, String placeholderSuffix) {
			String placeholder = concatFunction.render(Arrays.asList("'%'", wrapPlaceholder(originKey, placeholderPrefix, placeholderSuffix)));
			return new OperationParsedResult(fieldName, " not like ", placeholder, originKey);
		}
	},
	in {
		@Override
		public OperationParsedResult buildResult(String fieldName, String originKey, String placeholderPrefix, String placeholderSuffix) {
			return new OperationParsedResult(fieldName, " in ", "(" + wrapPlaceholder(originKey, placeholderPrefix, placeholderSuffix) + ")", originKey);
		}
	},
	not_in {
		@Override
		public OperationParsedResult buildResult(String fieldName, String originKey, String placeholderPrefix, String placeholderSuffix) {
			return new OperationParsedResult(fieldName, " not in ", "(" + wrapPlaceholder(originKey, placeholderPrefix, placeholderSuffix) + ")", originKey);
		}
	},
	is_null {
		@Override
		public OperationParsedResult buildResult(String fieldName, String originKey, String placeholderPrefix, String placeholderSuffix) {
			return new OperationParsedResult(fieldName, " is null ", "", wrapPlaceholder(originKey, placeholderPrefix, placeholderSuffix));
		}
	},
	is_not_null {
		@Override
		public OperationParsedResult buildResult(String fieldName, String originKey, String placeholderPrefix, String placeholderSuffix) {
			return new OperationParsedResult(fieldName, " is not null ", "", wrapPlaceholder(originKey, placeholderPrefix, placeholderSuffix));
		}
	},
	exists {
		@Override
		public OperationParsedResult buildResult(String fieldName, String originKey, String placeholderPrefix, String placeholderSuffix) {
			int index = originKey.lastIndexOf( __ );
			String existsClause = originKey.substring(0, index);
			return new OperationParsedResult("", " exists (" + existsClause + ") ", "", originKey);
		}
	},
	not_exists {
		@Override
		public OperationParsedResult buildResult(String fieldName, String originKey, String placeholderPrefix, String placeholderSuffix) {
			int index = originKey.lastIndexOf( __ );
			String existsClause = originKey.substring(0, index);
			return new OperationParsedResult("", " not exists (" + existsClause + ")", "", originKey);
		}
	},
	sub_query {
		@Override
		public OperationParsedResult buildResult(String fieldName, String originKey, String placeholderPrefix, String placeholderSuffix) {
			int index = originKey.lastIndexOf( __ );
			String subQuery = originKey.substring(0, index);
			return new OperationParsedResult("", "(" + subQuery + ")", "", originKey);
		}
	}
	;
	
	static final String __ = "__";
	
	private static VarArgsSQLFunction concatFunction = new VarArgsSQLFunction("concat(",",", ")");
	
	private static final String DEFAULT_PLACEHOLDER_PREFIX = ":";
	
	private static final String DEFAULT_PLACEHOLDER_SUFFIX = "";
	
	protected String wrapPlaceholder(String placeholder, String prefix, String suffix) {
		return prefix + placeholder + suffix;
	}
	
	public OperationParsedResult buildResult(String fieldName, String originKey) {
		return buildResult(fieldName, originKey, DEFAULT_PLACEHOLDER_PREFIX, DEFAULT_PLACEHOLDER_SUFFIX);
	}
	
	public abstract OperationParsedResult buildResult(String fieldName, String originKey, String placeholderPrefix, String placeholderSuffix);
	
}
