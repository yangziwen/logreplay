define(function(require, exports, module) {
	
	"use strict";

	require('jquery.tmpl');
	var $ = require('jquery'),
		common = require('app/common'),
		moment = require('moment');
	
	function initSystemInfo() {
		return $.get(CTX_PATH + '/monitor/systemInfo', function(data) {
			if(data.code !== 0 || !data.response) {
				return;
			}
			$('#J_systemInfoTmpl').tmpl(data.response)
				.appendTo('#J_systemInfoTbody')
		});
	}
	
	function refreshMemoryChart() {
		fetchMemoryData().done(drawMemoryChart);
	}
	
	function drawMemoryChart(data) {
		drawJvmMemoryChart(data);
		drawSystemMemoryChart(data);
	}
	
	function drawJvmMemoryChart(data) {
		var option = {
			tooltip: {
				trigger: 'axis'
			},
			legend: {
				data: ['used memory', 'used non-heap memory']
			},
			toolbox: {
				show: false
			},
			xAxis: [{
				type: 'category',
				boundaryGap: false,
				data: $.map(data.usedMemoryDataList, function(data) {
					return moment(data.key).format('HH:mm');
				})
			}],
			yAxis: [{
				type: 'value',
				axisLabel: {
					formatter: '{value} MB'
				}
			}],
			series: [{
				name: 'used memory',
				type: 'line',
				data: $.map(data.usedMemoryDataList, function(data) {
					return (data.value / 1024 / 1024).toFixed(2);
				})
			}, {
				name: 'used non-heap memory',
				type: 'line',
				data: $.map(data.usedNonHeapMemoryDataList, function(data) {
					return (data.value / 1024 / 1024).toFixed(2);
				})
			}]
			
		};
		echarts
			.init($('#J_jvmMemoryChart')[0], 'macarons')
			.setOption(option);
	}
	
	function drawSystemMemoryChart(data) {
		var option = {
			tooltip: {
				trigger: 'axis'
			},
			legend: {
				data: ['used physical memory', 'used swap space']
			},
			toolbox: {
				show: false
			},
			xAxis: [{
				type: 'category',
				boundaryGap: false,
				data: $.map(data.usedMemoryDataList, function(data) {
					return moment(data.key).format('HH:mm');
				})
			}],
			yAxis: [{
				type: 'value',
				axisLabel: {
					formatter: '{value} GB'
				}
			}],
			series: [{
				name: 'used physical memory',
				type: 'line',
				data: $.map(data.usedPhysicalMemoryDataList, function(data) {
					return (data.value / Math.pow(1024, 3)).toFixed(2);
				})
			}, {
				name: 'used swap space',
				type: 'line',
				data: $.map(data.usedSwapSpaceDataList, function(data) {
					return (data.value / Math.pow(1024, 3)).toFixed(2);
				})
			}]
			
		};
		echarts
			.init($('#J_systemMemoryChart')[0], 'macarons')
			.setOption(option);
	}
	
	function fetchMemoryData() {
		var d = $.Deferred();
		$.get(CTX_PATH + '/monitor/memoryData', function(data) {
			if(data.code !== 0 || !data.response) {
				d.reject(data);
			} else {
				d.resolve(data.response);
			}
		});
		return d.promise();
	}
	
	function init() {
		initSystemInfo();
		refreshMemoryChart();
	}
	
	module.exports = {init: init};
	
});