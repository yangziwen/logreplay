package com.sogou.map.logreplay.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

public class ChecksumUtil {
	
	private static final char[] chars64 = buildChars64();
	
	private static final Map<Character, Integer> chars64Dict = buildChars64Dict(chars64);
	
	private ChecksumUtil() {}
	
	private static char[] buildChars64() {
		char[] chars = new char[64];
		int i = 0;
		for(char c = '0'; c <= '9'; c++) {
			chars[i++] = c;
		}
		for(char c = 'a'; c <= 'z'; c++) {
			chars[i++] = c;
		}
		for(char c = 'A'; c <= 'Z'; c++) {
			chars[i++] = c;
		}
		chars[i++] = '!';
		chars[i++] = '$';
		return chars;
	}
	
	private static Map<Character, Integer> buildChars64Dict(char[] chars64) {
		Map<Character, Integer> dict = new HashMap<Character, Integer>();
		for(int i = 0, l = chars64.length; i < l; i++) {
			dict.put(chars64[i], i);
		}
		return dict;
	}
	
	public static String convertHexTo64(String hex) {
		LinkedList<String> strs = new LinkedList<String>();
		int len = hex.length(), step = 3;
		for(int i = 0; i < len; i += step) {
			int begin = Math.max(0, len - i - step);
			int end = len - i;
			strs.addFirst(hex.substring(begin, end));
		}
		int i = 0, capacity = ( len / 3 + (len % 3 > 0? 1: 0) ) * 2;
		char[] chars = new char[capacity];
		for(String str: strs) {
			int value = Integer.valueOf(str, 16);
			chars[i++] = chars64[value >> 6];
			chars[i++] = chars64[value & 0x3f];
		}
		if(chars[0] == '0') {
			chars = Arrays.copyOfRange(chars, 1, capacity);
		}
		return String.valueOf(chars);
	}
	
	public static String convert64ToHex(String data64) {
		LinkedList<String> strs = new LinkedList<String>();
		int len = data64.length(), step = 2;
		for(int i = 0; i < len; i += step) {
			int begin = Math.max(0,  len - i - step);
			int end = len - i;
			strs.addFirst(data64.substring(begin, end));
		}
		int i = 0, capacity = (len / 2 + (len % 2)) * 3;
		char[] chars = new char[capacity];
		for(String str: strs) {
			int value = convert64ToInt(str);
			chars[i++] = chars64[value >> 8];		// 64进制的前16位标识符与16进制保持一致
			chars[i++] = chars64[value >> 4 & 0xf];
			chars[i++] = chars64[value & 0xf];
		}
		i = 0;
		while(chars[i] == '0') {
			i++;
		}
		if(i > 0) {
			chars = Arrays.copyOfRange(chars, i, capacity);
		}
		return String.valueOf(chars);
	}
	
	private static int convert64ToInt(String data64) {
		int value = 0;
		for(int i = 0, l = data64.length(); i < l; i++) {
			char c = data64.charAt(l - i - 1);
			value += chars64Dict.get(c) << (6 * i);
		}
		return value;
	}
	
	public static String sha1Hex(String data) {
		return DigestUtils.sha1Hex(data);
	}
	
	public static String sha1Hex(byte[] data) {
		return DigestUtils.sha1Hex(data);
	}
	
	public static String sha1_64(String data) {
		return convertHexTo64(sha1Hex(data));
	}
	
	public static String sha1_64(byte[] data) {
		return convertHexTo64(sha1Hex(data));
	}

	public static String md5Hex(String data) {
		return DigestUtils.md5Hex(data);
	}
	
	public static String md5Hex(byte[] data) {
		return DigestUtils.md5Hex(data);
	}
	
	public static String md5_64(String data) {
		return convertHexTo64(md5Hex(data));
	}
	
	public static String md5_64(byte[] data) {
		return convertHexTo64(md5Hex(data));
	}
	
}
