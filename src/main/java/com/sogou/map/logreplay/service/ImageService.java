package com.sogou.map.logreplay.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.map.logreplay.bean.Image;
import com.sogou.map.logreplay.dao.ImageDao;
import com.sogou.map.logreplay.dao.base.QueryParamMap;

@Service
public class ImageService {
	
	@Autowired
	private ImageDao imageDao;

	public void createImage(Image image) {
		imageDao.save(image);
	}
	
	public Image getImageById(Long id) {
		return imageDao.getById(id);
	}
	
	public Image getImageByChecksum(String checksum) {
		return imageDao.first(new QueryParamMap().addParam("checksum", checksum));
	}
	
	public List<Image> getImageListResult(Map<String, Object> params) {
		return imageDao.list(params);
	}
	
	public int batchSaveImageList(List<Image> imageList) {
		return imageDao.batchSave(imageList);
	}
	
}
