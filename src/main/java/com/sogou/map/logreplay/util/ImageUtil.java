package com.sogou.map.logreplay.util;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;


public class ImageUtil {
	
	private ImageUtil() {}
	
	public static BufferedImage zoomImage(BufferedImage image, double scale) {
		return zoomImage(image, scale, scale);
	}
	
	public static BufferedImage zoomImage(BufferedImage image, double scaleX, double scaleY) {
		AffineTransformOp transformOp = new AffineTransformOp(AffineTransform.getScaleInstance(scaleX, scaleY), null);
		return transformOp.filter(image, null);
	}
	
	public static byte[] toByteArray(BufferedImage bufferedImage, String format) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream(4096);
		ImageIO.write(bufferedImage, format, output);
		return output.toByteArray();
	}
	
}
