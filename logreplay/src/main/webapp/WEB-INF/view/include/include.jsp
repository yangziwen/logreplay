<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx_path" value="${pageContext.request.contextPath}" />
<%-- 
static_path是静态文件的根路径，目前使用contextPath。
今后如果需要静态文件分离，则可考虑将此配置修改为静态文件服务器的路径
 --%>
<c:set var="static_path" value="${pageContext.request.contextPath}" />
<c:set var="request_uri" value="${pageContext.request.requestURI}"/>
<c:set var="static_version" value="20150421" /><%-- 静态文件的版本号 --%>