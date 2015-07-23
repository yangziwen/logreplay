package com.sogou.map.logreplay.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sogou.map.logreplay.bean.Avatar;
import com.sogou.map.logreplay.bean.Image;
import com.sogou.map.logreplay.bean.Image.AvatarType;
import com.sogou.map.logreplay.controller.base.BaseController;
import com.sogou.map.logreplay.dao.base.QueryParamMap;
import com.sogou.map.logreplay.exception.LogReplayException;
import com.sogou.map.logreplay.service.AvatarService;
import com.sogou.map.logreplay.service.ImageService;
import com.sogou.map.logreplay.util.AuthUtil;
import com.sogou.map.logreplay.util.ImageUtil;

@Controller
@RequestMapping("/image")
public class ImageController extends BaseController {
	
	public static final String DEFAULT_IMAGE_FORMAT = "jpg";
	
	public static final int DEFAULT_CACHE_SECONDS = 28800;
	
	private static final String HEADER_CACHE_CONTROL = "Cache-Control";
	
	private static final String HEADER_EXPIRES = "Expires";
	
	private static final String HEADER_LAST_MODIFIED = "Last-Modified";
	
	@Autowired
	private ImageService imageService;
	
	@Autowired
	private AvatarService avatarService;
	
	/**
	 * 按id获取图片
	 */
	@RequestMapping("{id:\\d+}")
	public void getImageById(
			@PathVariable("id") Long id,
			NativeWebRequest webRequest,
			HttpServletResponse response) throws ServletException, IOException {
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		Image image = null;
		File imageFile = null;
		if(id == null 
				|| (image = imageService.getImageById(id)) == null
				|| !(imageFile = new File(image.getFilepath())).exists()) {
			throw new NoSuchRequestHandlingMethodException(request);
		}
		
		outputImage(imageFile, webRequest, response);
		
	}
	
	/**
	 * 按路径获取图片
	 * 正式情况下可使用独立的图片服务器提供服务
	 * 修改jndi的配置即可调整图片url根路径
	 */
	@RequestMapping("/{year:\\d{4}}/{month:\\d{2}}/{date:\\d{2}}/{filename:[^.]{40}\\.\\w+}")
	public void getImageByPath(
			@PathVariable("year") String year,
			@PathVariable("month") String month,
			@PathVariable("date") String date,
			@PathVariable("filename") String filename,
			NativeWebRequest webRequest,
			HttpServletResponse response) throws ServletException, IOException {
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		filename = filename.toLowerCase();
		String filepath = StringUtils.join(new String[]{Image.IMAGE_BASE_PATH, year, month, date, filename}, "/");
		File imageFile = new File(filepath);
		if(!imageFile.exists()) {
			throw new NoSuchRequestHandlingMethodException(request);
		}
		
		outputImage(imageFile, webRequest, response);
		
	}
	
	private void setContentHeaders(String mimeType, int length, HttpServletResponse response) {
		if(StringUtils.isNotBlank(mimeType)) {
			response.setContentType(mimeType);
		}
		response.setContentLength(length);
	}
	
	private void setCacheHeaders(HttpServletResponse response, int seconds, boolean mustRevalidate) {
		response.setDateHeader(HEADER_EXPIRES, System.currentTimeMillis() + seconds * 1000L);
		String headerValue = "max-age=" + seconds;
		if(mustRevalidate) {
			headerValue += ", must-revalidate";
		}
		response.setHeader(HEADER_CACHE_CONTROL,  headerValue);
	}
	
	private void outputImage(File imageFile, NativeWebRequest webRequest, HttpServletResponse response) {
		long lastModified = imageFile.lastModified();
		if(webRequest.checkNotModified(lastModified)) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return;
		}
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		response.setDateHeader(HEADER_LAST_MODIFIED, lastModified);
		ServletContext servletContext = request.getSession().getServletContext();
		String mimeType = servletContext.getMimeType(imageFile.getName());
		setContentHeaders(mimeType, Long.valueOf(imageFile.length()).intValue(), response);
		setCacheHeaders(response, DEFAULT_CACHE_SECONDS, false);
		
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(imageFile);
			output = response.getOutputStream();
			IOUtils.copy(input, output);
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(output);
		}
	}
	
	/**
	 * 获取用户头像
	 */
	@RequestMapping(value = "/avatar/{userId:\\d+}", method = RequestMethod.GET)
	public void getAvatar(
			@PathVariable("userId") Long userId,
			@RequestParam(defaultValue = Image.TYPE_MIDDLE) String type,
			NativeWebRequest webRequest,
			HttpServletResponse response) throws ServletException, IOException {
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		Avatar avatar = avatarService.getAvatarByUserIdAndType(userId, type);
		Image image = avatar != null? imageService.getImageById(avatar.getImageId()): null;
		File imageFile = null;
		if(image == null || !(imageFile = new File(image.getFilepath())).exists()) {
			// 替换成默认图片
			response.sendRedirect(request.getContextPath() + Avatar.DEFAULT_AVATAR);
			return;
		}
		
		outputImage(imageFile, webRequest, response);
	}
	
	/**
	 * 基于rawImage剪裁后提交新头像
	 */
	@ResponseBody
	@RequestMapping(value = "avatar", method = RequestMethod.POST)
	public ModelMap submitAvatar(
			@RequestParam Long imageId, 
			@RequestParam int left, 
			@RequestParam int top, 
			@RequestParam int width, 
			@RequestParam int height,
			@RequestParam int imgWidth,
			@RequestParam int imgHeight) throws IOException {
		if(left < 0 || top < 0 || width <= 0 || height <= 0 || imgWidth <= 0 || imgHeight <= 0 || imageId == null) {
			throw LogReplayException.invalidParameterException("Parameters are invalid!");
		}
		Image image = imageService.getImageById(imageId);
		if(image == null ) {
			throw LogReplayException.notExistException("Image[%d] does not exist!", imageId);
		}
		File imageFile = new File(image.getFilepath());
		if(!imageFile.exists()) {
			throw LogReplayException.notExistException("Failed to find file of Image[%d]", image.getId());
		}
		// 切出3种尺寸的头像
		double ratio = image.getWidth() * 1D / imgWidth;
		BufferedImage cuttedImage = ImageIO.read(imageFile)
				.getSubimage(toInt(left * ratio), toInt(top * ratio), toInt(width * ratio), toInt(height * ratio));
		List<Image> avatarImageList = buildAvatarImages(cuttedImage, DEFAULT_IMAGE_FORMAT);
		
		// 按校验和检查并丢弃已存在的图片
		List<String> checksumList = Lists.transform(avatarImageList, new Function<Image, String>() {
			@Override
			public String apply(Image image) {
				return image.getChecksum();
			}
		});
		List<Image> prevImageList = CollectionUtils.isEmpty(checksumList)? Collections.<Image>emptyList()
				: imageService.getImageListResult(new QueryParamMap().addParam("checksum__in", checksumList));
		Map<String, Image> prevImageMap = Maps.uniqueIndex(prevImageList, new Function<Image, String>(){
			@Override
			public String apply(Image image) {
				return image.getChecksum();
			}
		});
		
		Iterator<Image> avatarImageIter = avatarImageList.iterator();
		while(avatarImageIter.hasNext()) {
			if(prevImageMap.containsKey(avatarImageIter.next().getChecksum())) {
				avatarImageIter.remove();
			}
		}
		
		try {
			for(Image avatarImage: avatarImageList) {
				persistImage(avatarImage);
			}
//			imageService.batchSaveImageList(avatarImageList);
			for(Image avatarImage: avatarImageList) {
				imageService.createImage(avatarImage);
			}
			
			avatarImageList.addAll(prevImageList);
			List<Avatar> avatarList = buildAvatarList(avatarImageList);
			avatarService.renewAvatars(avatarList, AuthUtil.getCurrentUserId());
			return successResult("Avatars are updated successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			throw LogReplayException.operationFailedException("Failed to update avatars!");
		}
	}
	
	private List<Avatar> buildAvatarList(List<Image> avatarImageList) {
		List<Avatar> avatarList = new ArrayList<Avatar>();
		Long userId = AuthUtil.getCurrentUser().getId();
		for(Image image: avatarImageList) {
			avatarList.add(new Avatar(userId, image.getId(), image.getType()));
		}
		return avatarList;
	}
	
	private List<Image> buildAvatarImages(BufferedImage image, String format) throws IOException {
		if(StringUtils.isBlank(format)) {
			format = DEFAULT_IMAGE_FORMAT;
		}
		List<Image> avatarList = new ArrayList<Image>();
		int width = image.getWidth(), height = image.getHeight();
		Long creatorId = AuthUtil.getCurrentUser().getId();
		for(AvatarType avatarType: AvatarType.values()) {
			double scaleX = avatarType.getWidth() * 1D / width;
			double scaleY = avatarType.getHeight() * 1D / height;
			BufferedImage zoomedImage = ImageUtil.zoomImage(image, scaleX, scaleY);
			byte[] bytes = ImageUtil.toByteArray(zoomedImage, format);
			
			Image avatar = new Image.Builder()
				.creatorId(creatorId)
				.format(format)
				.width(avatarType.getWidth())
				.height(avatarType.getHeight())
				.type(avatarType.name())
				.bytes(bytes)
				.build();
			avatarList.add(avatar);
		}
		return avatarList;
	}

	/**
	 * 上传rawImage
	 */
	@ResponseBody
	@RequestMapping(value = "/upload", method = RequestMethod.POST, params = "type=raw")
	public Map<String, Object> uploadRawImage(MultipartFile file) {
		try {
			Image image = buildRawImage(file, DEFAULT_IMAGE_FORMAT);
			Image prevImage = imageService.getImageByChecksum(image.getChecksum());
			if(prevImage != null && prevImage.getSize().equals(image.getSize())) {
				return successResult(prevImage);
			}
			persistImage(image);
			imageService.createImage(image);
			return successResult(image);
		} catch (LogReplayException lre) {
			throw lre;
		} catch (Exception e) {
			e.printStackTrace();
			throw LogReplayException.operationFailedException("Failed to persist image[%s]", file.getName());
		}
	}
	
	private Image buildRawImage(MultipartFile file, String format) throws IOException {
		if(StringUtils.isBlank(format)) {
			format = FilenameUtils.getExtension(file.getName());
		}
		BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
		byte[] bytes = ImageUtil.toByteArray(bufferedImage, format);
		
		return new Image.Builder()
			.creatorId(AuthUtil.getCurrentUser().getId())
			.format(format)
			.bytes(bytes)
			.width(bufferedImage.getWidth())
			.height(bufferedImage.getHeight())
			.type(Image.TYPE_RAW)
			.build();
	}
	
	private void persistImage(Image image) throws IOException {
		File imageFile = new File(image.getFilepath());
		FileUtils.writeByteArrayToFile(imageFile, image.getBytes());
	}
	
	private int toInt(double value) {
		return Double.valueOf(value).intValue();
	}
	
}
