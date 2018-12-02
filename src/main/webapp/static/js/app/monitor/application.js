define(function(require, exports, module) {
	
	"use strict";

	require('jquery.tmpl');
	var $ = require('jquery'),
		common = require('app/common'),
		moment = require('moment');
	
	function initShowTypeBtnGroup() {
		$('#J_showTypeBtnGroup').on('change', 'label', function() {
			refreshCharts();
		});
	}
	
	function refreshCharts() {
		fetchAppData().done(drawCharts);
	}
	
	function drawCharts(data) {
		drawThreadChart(data);
		drawHttpChart(data);
		drawClassChart(data);
		drawSqlChart(data);
	}
	
	var defaultToolTipOption = {
		trigger: 'axis',
		backgroundColor: 'rgba(255, 255, 255, 0.6)',
		borderColor: '#ccc',
		borderWidth: 1,
		textStyle: {
			color: '#666'
		}
	};
	
	var defaultGridOption = {
		y: 30,
		x: 50,
		x2: 50
	};
	
	var showTypeFormats = {
		halfDay: 'HH:mm',
		day: 'HH:mm',
		week: 'ddd HH:mm',
		month: 'Do HH:mm'
	};
	
	function drawThreadChart(data) {
		var option = {
			tooltip: $.extend({
				formatter: function(params) {
					var data = params[0]
					return [
						'时间：' + data[1],
						'线程数：' + data[2]
					].join('<br/>');
				}
			}, defaultToolTipOption),
			title: {
				text: '线程状态',
				x: 'center'
			},
			legend: {
				data: ['线程数'],
				y: 'bottom'
			},
			toolbox: {
				show: false
			},
			grid: defaultGridOption,
			xAxis: [{
				name: '时间',
				type: 'category',
				boundaryGap: false,
				data: $.map(data.threadCountDataList, function(d) {
					return moment(d.key).format(showTypeFormats[data.showType]);
				})
			}],
			yAxis: [{
				name: '线程数',
				type: 'value'
			}],
			series: [{
				name: '线程数',
				type: 'line',
				data: $.map(data.threadCountDataList, function(data) {
					return parseInt(data.value);
				})
			}]
			
		};
		echarts
			.init($('#J_threadChart')[0], 'macarons')
			.setOption(option);
	}
	
	function drawHttpChart(data) {
		var maxCnt = 0;
		var httpCntList = $.map(data.httpHitsRateDataList, function(data) {
			var cnt = parseInt(data.value);
			maxCnt < cnt && (maxCnt = cnt);
			return cnt;
		});
		
		var option = {
			tooltip: $.extend({
				formatter: function(params) {
					var data = params[0]
					return [
						'时间：' + data[1],
						'平均请求数：' + data[2]
					].join('<br/>');
				}
			}, defaultToolTipOption),
			title: {
				text: 'http状态',
				x: 'center'
			},
			legend: {
				data: ['http请求数'],
				y: 'bottom'
			},
			toolbox: {
				show: false
			},
			grid: defaultGridOption,
			xAxis: [{
				name: '时间',
				type: 'category',
				boundaryGap: false,
				data: $.map(data.httpSessionsDataList, function(d) {
					return moment(d.key).format(showTypeFormats[data.showType]);
				})
			}],
			yAxis: [{
				name: '请求数',
				type: 'value',
				min: 0,
				max: parseInt(maxCnt / 4 * 5) + 1
			}],
			series: [{
				name: 'http请求数',
				type: 'line',
				data: httpCntList
			}]
			
		};
		echarts
			.init($('#J_httpChart')[0], 'macarons')
			.setOption(option);
	}
	
	function drawClassChart(data) {
		
		var option = {
			tooltip: $.extend({
				formatter: function(params) {
					var data = params[0]
					return [
						'时间：' + data[1],
						'类加载数：' + data[2]
					].join('<br/>');
				}
			}, defaultToolTipOption),
			title: {
				text: '类加载状态',
				x: 'center'
			},
			legend: {
				data: ['类加载数'],
				y: 'bottom'
			},
			toolbox: {
				show: false
			},
			grid: defaultGridOption,
			xAxis: [{
				name: '时间',
				type: 'category',
				boundaryGap: false,
				data: $.map(data.loadedClassesCountDataList, function(d) {
					return moment(d.key).format(showTypeFormats[data.showType]);
				})
			}],
			yAxis: [{
				name: '类加载数',
				type: 'value'
			}],
			series: [{
				name: '类加载数',
				type: 'line',
				data: $.map(data.loadedClassesCountDataList, function(data) {
					return parseInt(data.value);
				})
			}]
			
		};
		echarts
			.init($('#J_classChart')[0], 'macarons')
			.setOption(option);
	}
	
	function drawSqlChart(data) {
		var option = {
			tooltip: $.extend({
				formatter: function(params) {
					var sqlHitsRate = params[0],
						transactionsRate = params[1];
					return [
						'时间：' + sqlHitsRate[1],
						'sql数：' + sqlHitsRate[2],
						'事务数：' + transactionsRate[2]
					].join('<br/>');
				}
			}, defaultToolTipOption),
			title: {
				text: '数据库访问状态',
				x: 'center'
			},
			legend: {
				data: ['sql数', '事务数'],
				y: 'bottom'
			},
			toolbox: {
				show: false
			},
			grid: defaultGridOption,
			xAxis: [{
				name: '时间',
				type: 'category',
				boundaryGap: false,
				data: $.map(data.threadCountDataList, function(d) {
					return moment(d.key).format(showTypeFormats[data.showType]);
				})
			}],
			yAxis: [{
				name: '数量',
				type: 'value'
			}],
			series: [{
				name: 'sql数',
				type: 'line',
				data: $.map(data.sqlHitsRateDataList, function(data) {
					return parseInt(data.value);
				})
			}, {
				name: '事务数',
				type: 'line',
				data: $.map(data.transactionsRateDataList, function(data) {
					return parseInt(data.value);
				})
			}]
			
		};
		echarts
			.init($('#J_sqlChart')[0], 'macarons')
			.setOption(option);
	}
	
	function getShowTypeParam(showType) {
		var now = moment();
		switch(showType) {
			case 'halfDay': return {
				startTime: moment(now).subtract(12, 'h').format('x'),
				endTime: now.format('x'),
				step: 300
			}
			case 'day': return {
				startTime: moment(now).subtract(24, 'h').format('x'),
				endTime: now.format('x'),
				step: 1800
			}
			case 'week': return {
				startTime: moment(now).subtract(7, 'd').format('x'),
				endTime: now.format('x'),
				step: 3600
			}
			case 'month': return {
				startTime: moment(now).subtract(1, 'M').format('x'),
				endTime: now.format('x'),
				step: 3600
			}
			default: return {}
		}
	}
	
	function fetchAppData() {
		var showType = $('#J_showTypeBtnGroup input:checked').val();
		var showTypeParam = getShowTypeParam(showType);
		showTypeParam.showType = showType;
		var d = $.Deferred();
		$.get(CTX_PATH + '/monitor/appData', showTypeParam, function(data) {
			if (data.code !== 0 || !data.response) {
				d.reject(data);
			} else {
				d.resolve(data.response);
			}
		});
		return d.promise();
	}
	
	function init() {
		initShowTypeBtnGroup();
		refreshCharts();
	}
	
	module.exports = {init: init};
	
});
