package com.sogou.map.logreplay.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.sogou.map.logreplay.bean.Avatar;
import com.sogou.map.logreplay.dao.base.QueryParamMap;
import com.sogou.map.logreplay.mappers.AvatarMapper;

@Service
public class AvatarService {
	
	@Autowired
	private AvatarMapper avatarMapper;
	
	public void saveAvatar(Avatar avatar) {
		avatarMapper.save(avatar);
	}
	
	@Transactional
	public void renewAvatars(List<Avatar> avatarList, Long userId) {
		if(CollectionUtils.isEmpty(avatarList)) {
			return;
		}
		avatarMapper.deleteByUserId(userId);
		avatarMapper.batchSave(avatarList);
	}
	
	
	public Avatar getAvatarByUserIdAndType(Long userId, String type) {
		return avatarMapper.first(new QueryParamMap().addParam("userId", userId).addParam("type", type));
	}
}
