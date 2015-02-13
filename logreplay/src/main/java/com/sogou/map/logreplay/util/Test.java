package com.sogou.map.logreplay.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sogou.map.mengine.common.tool.impl.encode.URLDecoder;


/**
 * 日志解析测试
 */
public class Test {

	public static void main(String[] args) throws Exception {
		File file = new File("d:/日志回放系统/oneline.txt");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = reader.readLine();
		IOUtils.closeQuietly(reader);
		JSONObject jsonObj = JSON.parseObject(line);
//		System.out.println(line);
//		System.out.println(jsonObj.get("e"));
//		System.out.println(jsonObj.get("info"));
//		JSONArray infos = JSON.parseArray(jsonObj.getString("info"));
//		for(int i = 0, l = infos.size(); i < l; i++) {
//			System.out.println(infos.getJSONObject(i));
//		}
		
		
		file = new File("d:/日志回放系统/test2.txt");
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		line = reader.readLine();
		String prefixFlag = "GET /pv.gif?";
		int beginPos = line.indexOf(prefixFlag) + prefixFlag.length();
		int endPos = line.lastIndexOf("} HTTP/") + 1;
		String logContent = line.substring(beginPos, endPos);
		String infoPrefix = "&info=";
		int infoBeginPos = logContent.indexOf(infoPrefix) + infoPrefix.length();
		String commonInfoStr = logContent.substring(0, infoBeginPos);
		String infoStr = logContent.substring(infoBeginPos);
		
		Map<String, String> commonInfo = new HashMap<String, String>();
		for(String pair: commonInfoStr.split("&")) {
			if(StringUtils.isEmpty(pair)) {
				continue;
			}
			String[] pairArr = pair.split("=");
			if(pairArr.length < 2) {
				continue;
			}
			commonInfo.put(pairArr[0], URLDecoder.decode(URLDecoder.decode(pairArr[1])));
		}
		System.out.println(commonInfo);
		System.out.println("-------------");
		JSONObject info = JSON.parseObject(infoStr);
		System.out.println(info);
		JSONArray list = info.getJSONArray("l");
		JSONObject operInfo = null;
		for(int i = 0, l = list.size(); i < l; i++) {
			JSONObject obj = list.getJSONObject(i);
			if(obj.getInteger("e") != 6000) {
				continue;
			}
			operInfo = obj;
		}
		System.out.println(operInfo);
		JSONArray opers = JSONObject.parseArray(operInfo.getString("info"));
		List<Map<String, Object>> operList = new ArrayList<Map<String, Object>>();
		for(Object oper: opers) {
			JSONObject jsonOper = (JSONObject) oper;
			if(jsonOper.getInteger("tag") != null) {
				operList.add(jsonOper);
			}
		}
		System.out.println(operList);
		
//		System.out.println(UUID.randomUUID().toString().toLowerCase().replaceAll("-", "").toString());
	}
}

