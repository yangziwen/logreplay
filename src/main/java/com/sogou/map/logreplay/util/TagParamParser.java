package com.sogou.map.logreplay.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.sogou.map.logreplay.bean.ParamInfo;

/**
 * 根据tagInfoId,参数名和参数值来获取参数信息的帮助类
 */
public class TagParamParser {
	
	/** 
	 * 排除对p, tag, t, reqid, dataid, ucNavigateId等参数的解析
	 */
	private static final Pattern EXCLUDED_PARAM_KEYS_PATTERN = Pattern.compile("p|tag|t|.*id$", Pattern.CASE_INSENSITIVE);
	
	/** <tagInfoId, tagParams> **/
	private Map<Long, TagParamHolder> holderMap = new HashMap<Long, TagParamHolder>();
	
	public TagParamParser() {}
	
	public void addParamInfo(Long tagInfoId, ParamInfo paramInfo) {
		ensureHolder(tagInfoId).addParamInfo(paramInfo);
	}
	
	private TagParamHolder ensureHolder(Long tagInfoId) {
		TagParamHolder holder = holderMap.get(tagInfoId);
		if(holder != null) {
			return holder;
		}
		holder = new TagParamHolder();
		holder.tagInfoId = tagInfoId;
		holderMap.put(tagInfoId, holder);
		return holder;
	}
	
	public ParamInfo parse(Long tagInfoId, String paramName, String paramValue) {
		if(!holderMap.containsKey(tagInfoId)) {
			return null;
		}
		return holderMap.get(tagInfoId).parse(paramName, paramValue);
	}
	
	public List<String> getRequiredParamNameList(Long tagInfoId) {
		TagParamHolder tagParamHolder = holderMap.get(tagInfoId);
		if(tagParamHolder == null) {
			return Collections.emptyList();
		}
		List<String> list = new ArrayList<String>();
		for(ParamInfoHolder paramInfoHolder: tagParamHolder.holderMap.values()) {
			if(paramInfoHolder.merelyRequired()) {
				list.add(paramInfoHolder.paramName);
			}
		}
		return list;
	}
	
	/**
	 * 判断info中的某个参数是否需要解析
	 * 比如 p, tag, t, reqid之类的参数都是不需要被当做操作项的参数进行解析的
	 */
	public static boolean isParamKeyExcluded(String key) {
		return EXCLUDED_PARAM_KEYS_PATTERN.matcher(key).matches();
	}
	
	/**
	 * 同一tagInfo下的所有不同名称的param
	 */
	public static class TagParamHolder {
		
		private Long tagInfoId;
		
		public Long getTagInfoId() {
			return tagInfoId;
		}
		
		/** <paramName, ParamInfoHolder **/
		Map<String, ParamInfoHolder> holderMap = new HashMap<String, ParamInfoHolder>();
		
		public void addParamInfo(ParamInfo paramInfo) {
			ensureHolder(paramInfo.getName()).addParamInfo(paramInfo);;
		}
		
		public ParamInfoHolder ensureHolder(String paramName) {
			ParamInfoHolder holder = holderMap.get(paramName);
			if(holder != null) {
				return holder;
			}
			holder = new ParamInfoHolder();
			holder.paramName = paramName;
			holderMap.put(paramName, holder);
			return holder;
		}
		
		public ParamInfo parse(String paramName, String paramValue) {
			if(!holderMap.containsKey(paramName)) {
				return null;
			}
			return holderMap.get(paramName).parse(paramValue);
		}
		
	}
	
	/**
	 * 同一tagInfo，同一paramName下的不同paramValue
	 */
	public static class ParamInfoHolder {
		
		public static final String PARAM_VALUE_NOT_EXIST_DESCRIPTION = "参数的值不存在!";
		
		private String paramName;
		
		public String getParamName() {
			return paramName;
		}
		
		/** <paramValue, ParamInfo> **/
		Map<String, ParamInfo> paramInfoMap = new HashMap<String, ParamInfo>();
		
		/**
		 * 当某个paramName只对应一个paramValue且paramValue为""时
		 * 表明这个参数必须存在，但可以为任意值
		 */
		public boolean merelyRequired() {
			return paramInfoMap.size() == 1 && paramInfoMap.containsKey("");
		}
		
		public void addParamInfo(ParamInfo paramInfo) {
			paramInfoMap.put(paramInfo.getValue(), paramInfo);
		}
		
		public ParamInfo parse(String paramValue) {
			if(merelyRequired()) {
				return paramInfoMap.get("");
			}
			if(!paramInfoMap.containsKey(paramValue)) {
				return new ParamInfo(paramName, paramValue, PARAM_VALUE_NOT_EXIST_DESCRIPTION);
			}
			return paramInfoMap.get(paramValue);
		}
		
		/**
		 * 根据tagParamParser#parse方法返回的paramInfo来判断请求中携带的参数是否正确有效
		 * 基于操作项中的参数配置信息，请求中无效的参数包括
		 * 1) 参数缺失
		 * 2) 参数多余
		 */
		public static boolean isValid(ParamInfo paramInfo) {
			return paramInfo != null && !PARAM_VALUE_NOT_EXIST_DESCRIPTION.equals(paramInfo.getDescription());
		}
	}
	
}
