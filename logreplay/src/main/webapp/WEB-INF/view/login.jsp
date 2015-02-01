<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<%@ include file="./include/include.jsp" %>
<shiro:authenticated>
	<c:redirect url="${ctx_path}/home.htm"></c:redirect>
</shiro:authenticated>
<!DOCTYPE html>
<html lang="zh_CN">
<head>
    <title>登录页面</title>
	<%@include file="./include/includeCss.jsp" %>
</head>
<body>
<div>
    <div class="row" style="margin: 150px auto; width: 560px;">
        <div class="well center login-box" style="padding-bottom: 8px;">
        	<c:if test="${not empty errorMessage}">
	            <div class="alert alert-danger">${errorMessage}</div>
        	</c:if>
            <form class="form-horizontal" action="${ctx_path}/login.htm" method="post">
                <fieldset>
                    <div class="input-group input-group-lg">
                        <span class="input-group-addon"><i class="glyphicon glyphicon-user red"></i></span>
                        <input type="text" name="username" class="form-control" placeholder="用户名">
                    </div>
                    <div class="clearfix"></div><br>

                    <div class="input-group input-group-lg">
                        <span class="input-group-addon"><i class="glyphicon glyphicon-lock red"></i></span>
                        <input type="password" name="password" class="form-control" placeholder="密码">
                    </div>
                    <div class="clearfix"></div>

                    <div class="input-prepend" style="display:none;">
                        <label class="remember" for="remember"><input type="checkbox" id="remember"> Remember me</label>
                    </div>
                    <div class="clearfix"></div>

                    <p class="center" style="text-align: center; margin: 10px;">
                        <button type="submit" class="btn btn-primary">Login</button>
                    </p>
                </fieldset>
            </form>
        </div>
</div><!--/fluid-row-->

</div><!--/.fluid-container-->
<%-- <%@include file="./include/includeJs.jsp" %> --%>
</body>
</html>