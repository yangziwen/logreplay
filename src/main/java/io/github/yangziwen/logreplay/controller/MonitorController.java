package io.github.yangziwen.logreplay.controller;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.yangziwen.logreplay.controller.base.BaseController;
import io.github.yangziwen.logreplay.util.MonitorUtil;
import io.github.yangziwen.logreplay.util.MonitorUtil.ConsolFunc;
import io.github.yangziwen.logreplay.util.MonitorUtil.Data;
import io.github.yangziwen.logreplay.util.SystemInfoUtil;

@RestController
@RequestMapping("/monitor")
public class MonitorController extends BaseController {

	private static final String DEFAULT_START_TIME = "0";

	private static final String DEFAULT_END_TIME = "0";

	private static final String DEFAULT_STEP = "600";

	/**
	 * 获取系统相关参数
	 */
	@GetMapping("/systemInfo")
	public ModelMap getSystemInfo(HttpSession session) {
		String serverInfo = session.getServletContext().getServerInfo();
		ModelMap result = new ModelMap()
				.addAttribute("startTime", 				SystemInfoUtil.getStartTime())
				.addAttribute("javaVersion", 			SystemInfoUtil.getJavaVersion())
				.addAttribute("jvmVersion", 			SystemInfoUtil.getJvmVersion())
				.addAttribute("jvmArguments", 			SystemInfoUtil.getJvmArguments())
				.addAttribute("osInfo", 				SystemInfoUtil.getOsInfo())
				.addAttribute("osIcon", 				SystemInfoUtil.getOsIcon())
				.addAttribute("availableProcessors", 	SystemInfoUtil.getAvailableProcessors())
				.addAttribute("serverInfo", 			serverInfo)
				.addAttribute("serverIcon", 			SystemInfoUtil.getServerIcon(serverInfo))
				.addAttribute("pid", 					SystemInfoUtil.getPID())
		;
		return successResult(result);
	}

	/**
	 * 获取内存监控数据
	 * @param startTime	开始时间(毫秒时间戳)
	 * @param endTime	结束时间(毫秒时间戳)
	 * @param step		取样间隔(秒)
	 */
	@GetMapping("/memoryData")
	public ModelMap getMemoryData(
			@RequestParam(defaultValue = DEFAULT_START_TIME) long startTime,
			@RequestParam(defaultValue = DEFAULT_END_TIME) long endTime,
			@RequestParam(defaultValue = DEFAULT_STEP) long step,	// 默认取样间隔为10分钟
			String showType,
			HttpSession session) {

		if (startTime == 0) {	// 默认取一天前的时间
			startTime = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(12);
		}

		if (endTime == 0) {		// 默认取当前时间
			endTime = System.currentTimeMillis();
		}

		String application = MonitorUtil.getCurrentApplication(session.getServletContext());

		List<Data<Long, Double>> usedMemoryDataList = MonitorUtil.getDataList(application, "usedMemory", startTime, endTime, step, ConsolFunc.AVERAGE);

		List<Data<Long, Double>> usedNonHeapMemoryDataList = MonitorUtil.getDataList(application, "usedNonHeapMemory", startTime, endTime, step, ConsolFunc.AVERAGE);

		List<Data<Long, Double>> usedPhysicalMemoryDataList = MonitorUtil.getDataList(application, "usedPhysicalMemorySize", startTime, endTime, step, ConsolFunc.AVERAGE);

		List<Data<Long, Double>> usedSwapSpaceDataList = MonitorUtil.getDataList(application, "usedSwapSpaceSize", startTime, endTime, step, ConsolFunc.AVERAGE);

		ModelMap result = new ModelMap()
				.addAttribute("showType", showType)
				.addAttribute("usedMemoryDataList", usedMemoryDataList)
				.addAttribute("usedNonHeapMemoryDataList", usedNonHeapMemoryDataList)
				.addAttribute("usedPhysicalMemoryDataList", usedPhysicalMemoryDataList)
				.addAttribute("usedSwapSpaceDataList", usedSwapSpaceDataList)
		;

		return successResult(result);
	}

	@GetMapping("/appData")
	public ModelMap getAppData(
			@RequestParam(defaultValue = DEFAULT_START_TIME) long startTime,
			@RequestParam(defaultValue = DEFAULT_END_TIME) long endTime,
			@RequestParam(defaultValue = DEFAULT_STEP) long step,	// 默认取样间隔为10分钟
			String showType,
			HttpSession session) {

		if (startTime == 0) {	// 默认取一天前的时间
			startTime = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(12);
		}

		if (endTime == 0) {		// 默认取当前时间
			endTime = System.currentTimeMillis();
		}

		String application = MonitorUtil.getCurrentApplication(session.getServletContext());

		List<Data<Long, Double>> loadedClassesCountDataList = MonitorUtil.getDataList(application, "loadedClassesCount", startTime, endTime, step, ConsolFunc.AVERAGE);

		List<Data<Long, Double>> httpSessionsDataList = MonitorUtil.getDataList(application, "httpSessions", startTime, endTime, step, ConsolFunc.AVERAGE);

		List<Data<Long, Double>> httpHitsRateDataList = MonitorUtil.getDataList(application, "httpHitsRate", startTime, endTime, step, ConsolFunc.AVERAGE);

		List<Data<Long, Double>> sqlHitsRateDataList = MonitorUtil.getDataList(application, "sqlHitsRate", startTime, endTime, step, ConsolFunc.AVERAGE);

		List<Data<Long, Double>> transactionsRateDataList = MonitorUtil.getDataList(application, "transactionsRate", startTime, endTime, step, ConsolFunc.AVERAGE);

		List<Data<Long, Double>> threadCountDataList = MonitorUtil.getDataList(application, "threadCount", startTime, endTime, step, ConsolFunc.AVERAGE);

		ModelMap result = new ModelMap()
				.addAttribute("showType", showType)
				.addAttribute("loadedClassesCountDataList", loadedClassesCountDataList)
				.addAttribute("httpSessionsDataList", httpSessionsDataList)
				.addAttribute("httpHitsRateDataList", httpHitsRateDataList)
				.addAttribute("sqlHitsRateDataList", sqlHitsRateDataList)
				.addAttribute("transactionsRateDataList", transactionsRateDataList)
				.addAttribute("threadCountDataList", threadCountDataList)
		;

		return successResult(result);
	}


}
