package com.sogou.map.logreplay.audit;

import javax.servlet.http.HttpServletRequest;

import org.audit4j.core.MetaData;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sogou.map.logreplay.util.AuthUtil;
import com.sogou.map.logreplay.util.IPUtil;

public class AuditMetaData implements MetaData {

	private static final long serialVersionUID = 3113581221556423029L;
	
	private static final String UNKNOWN_IP = "unknown";

	@Override
	public String getActor() {
		return AuthUtil.getUsername();
	}

	@Override
	public String getOrigin() {
		try {
			RequestAttributes attribute = RequestContextHolder.currentRequestAttributes();
			if (attribute == null) {
				return UNKNOWN_IP;
			}
			HttpServletRequest request = ((ServletRequestAttributes) attribute).getRequest();
			return IPUtil.getIpAddr(request);
        }catch(Exception e){
            e.printStackTrace();
            return UNKNOWN_IP;
        }
	}

}
