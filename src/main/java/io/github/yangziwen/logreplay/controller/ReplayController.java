package io.github.yangziwen.logreplay.controller;

import java.security.Principal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.alibaba.fastjson.JSON;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;

import io.github.yangziwen.logreplay.dto.OperationRecordDto;

@Controller
public class ReplayController implements ApplicationListener<SessionDisconnectEvent> {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private ConcurrentMap<String, ReplayFilterWrapper> filters = new ConcurrentHashMap<>();

	@Autowired
	private AsyncEventBus eventBus;

	@Autowired
	private SimpMessagingTemplate messageTemplate;

	@PostConstruct
	public void register() {
		eventBus.register(this);
	}

	/**
	 * 开启客户端的日志实时播放
	 */
	@MessageMapping("/replay")
	public void replay(MessageHeaders headers, Principal user, String content) {
		String sessionId = headers.get("simpSessionId", String.class);
		ReplayFilter filter = JSON.parseObject(content, ReplayFilter.class);
		ReplayFilterWrapper wrapper = new ReplayFilterWrapper(sessionId, user.getName(), filter);
		filters.put(sessionId, wrapper);
		logger.info("user[{}] subscribed logreplay with session[{}]", user.getName(), sessionId);
	}

	/**
	 * 广播操作记录
	 */
	@Subscribe
	public void broadcast(OperationRecordDto record) {
		for (ReplayFilterWrapper filter : filters.values()) {
			if (filter.accept(record)) {
				messageTemplate.convertAndSendToUser(filter.getUsername(), "/queue/replay", record);
			}
		}
	}

	@Override
	public void onApplicationEvent(SessionDisconnectEvent event) {
		ReplayFilterWrapper wrapper = filters.remove(event.getSessionId());
		logger.info("user[{}] unsubscribed logreplay with session[{}]",
				wrapper != null ? wrapper.getUsername() : null, event.getSessionId());
	}

	public static class ReplayFilterWrapper {

		private String sessionId;

		private String username;

		private ReplayFilter filter;

		public ReplayFilterWrapper (String sessionId, String username, ReplayFilter filter) {
			this.sessionId = sessionId;
			this.username = username;
			this.filter = filter;
		}

		public String getSessionId() {
			return sessionId;
		}

		public void setSessionId(String sessionId) {
			this.sessionId = sessionId;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public ReplayFilter getFilter() {
			return filter;
		}

		public void setFilter(ReplayFilter filter) {
			this.filter = filter;
		}

		public boolean accept(OperationRecordDto record) {
			return filter.accept(record);
		}

		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
		}

	}

	public static class ReplayFilter {

		private Long productId;

		private String deviceId;

		private String uvid;

		private Integer pageNo;

		private Integer tagNo;

		private Integer originVersionSince;

		private Integer originVersionUntil;

		public Long getProductId() {
			return productId;
		}

		public void setProductId(Long productId) {
			this.productId = productId;
		}

		public String getDeviceId() {
			return deviceId;
		}

		public void setDeviceId(String deviceId) {
			this.deviceId = deviceId;
		}

		public String getUvid() {
			return uvid;
		}

		public void setUvid(String uvid) {
			this.uvid = uvid;
		}

		public Integer getPageNo() {
			return pageNo;
		}

		public void setPageNo(Integer pageNo) {
			this.pageNo = pageNo;
		}

		public Integer getTagNo() {
			return tagNo;
		}

		public void setTagNo(Integer tagNo) {
			this.tagNo = tagNo;
		}

		public Integer getOriginVersionSince() {
			return originVersionSince;
		}

		public void setOriginVersionSince(Integer originVersionSince) {
			this.originVersionSince = originVersionSince;
		}

		public Integer getOriginVersionUntil() {
			return originVersionUntil;
		}

		public void setOriginVersionUntil(Integer originVersionUntil) {
			this.originVersionUntil = originVersionUntil;
		}

		public boolean accept(OperationRecordDto record) {
			if (record == null) {
				return false;
			}
			if (productId != null && !productId.equals(record.getProductId()) ) {
				return false;
			}
			if (StringUtils.isNotBlank(deviceId) && !deviceId.equals(record.getDeviceId())) {
				return false;
			}
			if (StringUtils.isNotBlank(uvid) && !uvid.equals(record.getUvid())) {
				return false;
			}
			if (pageNo != null && !pageNo.equals(record.getPageNo())) {
				return false;
			}
			if (tagNo != null && tagNo.equals(record.getTagNo())) {
				return false;
			}
			if (originVersionSince != null && originVersionSince != 0 && originVersionSince > record.getVersion()) {
				return false;
			}
			if (originVersionUntil != null && originVersionUntil != 0 && originVersionUntil < record.getVersion()) {
				return false;
			}
			return true;
		}

		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
		}

	}

}
