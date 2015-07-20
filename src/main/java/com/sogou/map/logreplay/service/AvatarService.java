package com.sogou.map.logreplay.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.sogou.map.logreplay.bean.Avatar;
import com.sogou.map.logreplay.dao.AvatarDao;
import com.sogou.map.logreplay.dao.base.QueryParamMap;

@Service
public class AvatarService {

	@Autowired
	private AvatarDao avatarDao;
	
	public void saveAvatar(Avatar avatar) {
		avatarDao.save(avatar);
	}
	
	@Transactional
	public void renewAvatars(List<Avatar> avatarList, Long userId) {
		if(CollectionUtils.isEmpty(avatarList)) {
			return;
		}
		avatarDao.deleteByUserId(userId);
		avatarDao.batchSave(avatarList);
	}
	
	
	public Avatar getAvatarByUserIdAndType(Long userId, String type) {
		return avatarDao.first(new QueryParamMap().addParam("userId", userId).addParam("type", type));
	}
}
