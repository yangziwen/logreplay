package com.sogou.map.logreplay.enums;

/** 上传图片时产生的图片尺寸类型 **/
public enum ImageType {
	raw(0, 0),
	small(32, 32),
	middle(64, 64),
	large(128, 128)
	;

	public static final String DEFAULT_VALUE = "middle";

	private int width;

	private int height;

	private ImageType(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}
}
