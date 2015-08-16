package com.sogou.map.logreplay.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.map.logreplay.bean.Image;
import com.sogou.map.logreplay.dao.ImageDao;
import com.sogou.map.logreplay.mappers.ImageMapper;

@Service
public class ImageService {
	
	@Autowired
	private ImageDao imageDao;
	
	@Autowired
	private ImageMapper imageMapper;

	public void createImage(Image image) {
		imageMapper.save(image);
	}
	
	public Image getImageById(Long id) {
		return imageMapper.getById(id);
	}
	
	public Image getImageByChecksum(String checksum) {
		return imageMapper.getByChecksum(checksum);
	}
	
	public List<Image> getImageListResult(Map<String, Object> params) {
		return imageMapper.list(params);
	}
	
	public int batchSaveImageList(List<Image> imageList) {
		return imageMapper.batchSave(imageList);
	}
	
}
