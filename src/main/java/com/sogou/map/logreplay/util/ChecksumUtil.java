package com.sogou.map.logreplay.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class ChecksumUtil {
	
	private ChecksumUtil() {}
	
	public static String sha1Hex(String data) {
		return DigestUtils.sha1Hex(data);
	}
	
	public static String sha1Hex(byte[] data) {
		return DigestUtils.sha1Hex(data);
	}
	
	public static String sha1Base64(String data) {
		return Base64.encodeBase64URLSafeString(DigestUtils.sha1(data));
	}
	
	public static String sha1Base64(byte[] data) {
		return Base64.encodeBase64URLSafeString(DigestUtils.sha1(data));
	}

	public static String md5Hex(String data) {
		return DigestUtils.md5Hex(data);
	}
	
	public static String md5Hex(byte[] data) {
		return DigestUtils.md5Hex(data);
	}
	
	public static String md5Base64(String data) {
		return Base64.encodeBase64URLSafeString(DigestUtils.md5(data));
	}
	
	public static String md5Base64(byte[] data) {
		return Base64.encodeBase64URLSafeString(DigestUtils.md5(data));
	}
	
}
