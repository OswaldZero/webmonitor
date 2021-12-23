package com.webmonitor.wyh.statistics;

import java.util.HashMap;
import java.util.Map;

public class StatisticsManager {

	private static StatisticsManager sm = new StatisticsManager();
	
	private Map<String,Statistics> statisticsMap = new HashMap<String,Statistics>();
	
	private StatisticsManager() {}
	
	public static StatisticsManager newInstance() {
		return sm;
	}
	
	public void register(Statistics s){
		statisticsMap.put(s.getClass().getSimpleName(), s);
	}
	
	//清除所有统计数据
	public void cleanAll() {
		for (Statistics s : statisticsMap.values()) {
			s.clean();
		}
	}
}
