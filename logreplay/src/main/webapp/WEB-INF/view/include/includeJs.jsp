<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<script src="${static_path}/js/sea-modules/sea.js"></script>
<script>
var CTX_PATH = '${ctx_path}', STATIC_PATH = '${static_path}';
seajs.config({
	base: STATIC_PATH + '/js/',
	paths: {
		'jquery': STATIC_PATH + '/js/jquery',
		'bootstrap': STATIC_PATH + '/js/bootstrap',
		'app': STATIC_PATH + '/js/app'
	},
	alias: {
		'jquery': 'jquery/jquery.js',
		'jquery.tmpl': 'jquery/jquery.tmpl.js',
		'bootstrap': 'bootstrap/bootstrap.min.js',
	}
});
</script>