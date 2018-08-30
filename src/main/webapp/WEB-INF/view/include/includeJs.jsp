<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script src="${static_path}/js/sea-modules/sea.js"></script>
<script>
var CTX_PATH = '${ctx_path}', STATIC_PATH = '${static_path}';
seajs.config({
	base: STATIC_PATH + '/js/',
	paths: {
		'jquery': STATIC_PATH + '/js/jquery',
		'bootstrap': STATIC_PATH + '/js/bootstrap',
		'moment': STATIC_PATH + '/js/moment',
		'socket': STATIC_PATH + '/js/socket',
		'app': STATIC_PATH + '/js/app'
	},
	alias: {
		'jquery': 'jquery/jquery.js',
		'jquery.tmpl': 'jquery/jquery.tmpl.js',
		'jquery.validate': 'jquery/jquery.validate.js',
		'jquery.cookie': 'jquery/jquery.cookie.js',
		'jquery.imagetailor': 'jquery/jqueryImageTailor.js',
		'bootstrap': 'bootstrap/bootstrap.min.js',
		'bootstrap.pagebar': 'bootstrap/bootstrapPageBar.js',
		'bootstrap.uploadfilebtn': 'bootstrap/bootstrapUploadFileBtn.js',
		'bootstrap.browsefilebtn': 'bootstrap/bootstrapBrowseFileBtn.js',
		'bootstrap.datetimepicker': 'bootstrap/bootstrap-datetimepicker.js',
		'moment': 'moment/moment.js',
		'stomp': 'socket/stomp'
	}, 
	map: [[/^(.+\.js)/i, '$1?v=${static_version}' ]]
});
</script>
<script>
Date.prototype.format = function(format) {
	format || (format = 'yyyy-MM-dd');
	var o = {
		"M+" : this.getMonth() + 1, // month
		"d+" : this.getDate(), // day
		"h+" : this.getHours(), // hour
		"m+" : this.getMinutes(), // minute
		"s+" : this.getSeconds(), // second
		"q+" : Math.floor((this.getMonth() + 3) / 3), // quarter
		"S" : this.getMilliseconds()
	};
	if (/(y+)/.test(format)) {
		format = format.replace(RegExp.$1, (this.getFullYear() + "")
				.substr(4 - RegExp.$1.length));
	}
	for ( var k in o) {
		if (new RegExp("(" + k + ")").test(format)) {
			format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k]
					: ("00" + o[k]).substr(("" + o[k]).length));
		}
	}
	return format;
};
Date.format = function(timestamp, format) {
	var d;
	if(isNaN(d = parseInt(timestamp)) || !isFinite(d)) {
		return '';
	}
	return new Date(d).format(format);
};
</script>