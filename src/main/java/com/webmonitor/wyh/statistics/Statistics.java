package com.webmonitor.wyh.statistics;


import com.webmonitor.wyh.protocol.Protocol;
import com.webmonitor.wyh.utils.ColorRule;

public abstract class Statistics {
	
	private StatisticsManager sm;
	
	Class<? extends Protocol> detailProtocolClass = null;
	
	int colorIndex = ColorRule.colors.length-1;
	
	public Statistics() {
		sm = StatisticsManager.newInstance();
		sm.register(this);
	}

	public abstract void clean();
	
	public int getColorIndex() {
		return colorIndex;
	}

	public Class<? extends Protocol> getDetailProtocolClass() {
		return detailProtocolClass;
	}

	public void setDetailProtocolClass(Class<? extends Protocol> detailProtocolClass) {
		this.detailProtocolClass = detailProtocolClass;
	}
	
	

}
