package io.github.yangziwen.logreplay.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;


public class ImageUtil {
	
	private ImageUtil() {}
	
//	public static BufferedImage zoomImage(BufferedImage image, double scaleX, double scaleY) {
//		AffineTransformOp transformOp = new AffineTransformOp(AffineTransform.getScaleInstance(scaleX, scaleY), null);
//		return transformOp.filter(image, null);
//	}
	
	public static BufferedImage zoomImage(BufferedImage image, double scale) {
		return zoomImage(image, scale, scale);
	}
	
	public static BufferedImage zoomImage(BufferedImage image, double scaleX, double scaleY) {
		int scaleWidth = Double.valueOf(image.getWidth() * scaleX).intValue();
		int scaleHeight = Double.valueOf(image.getHeight() * scaleY).intValue();
		return zoomImage(image, scaleWidth, scaleHeight);
	}
	
	public static BufferedImage zoomImage(BufferedImage image, int length) {
		return zoomImage(image, length, length);
	}
	
	public static BufferedImage zoomImage(BufferedImage image, int width, int height) {
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);  
		newImage.getGraphics().drawImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0,  0, null); 
	    return newImage;
	}
	
	public static byte[] toByteArray(BufferedImage bufferedImage, String format) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream(4096);
		ImageIO.write(bufferedImage, format, output);
		return output.toByteArray();
	}
	
}
