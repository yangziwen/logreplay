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