package io.github.yangziwen.logreplay.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import io.github.yangziwen.logreplay.bean.Avatar;
import io.github.yangziwen.logreplay.bean.Image;
import io.github.yangziwen.logreplay.dao.AvatarDao;
import io.github.yangziwen.logreplay.dao.base.QueryParamMap;

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
	
	
	public Avatar getAvatarByUserIdAndType(Long userId, Image.Type type) {
		return avatarDao.first(new QueryParamMap().addParam("userId", userId).addParam("type", type));
	}
}
