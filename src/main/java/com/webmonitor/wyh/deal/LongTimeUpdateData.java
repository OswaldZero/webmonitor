package com.webmonitor.wyh.deal;



import com.webmonitor.wyh.frame.LongTimeMonitorFrame;
import com.webmonitor.wyh.statistics.Counter;
import com.webmonitor.wyh.utils.FormatTime;
import com.webmonitor.wyh.utils.Tool;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

//用于在长期监控模式下，定时更新报表面板数据
public class LongTimeUpdateData {

	private static LongTimeUpdateData ltud = new LongTimeUpdateData();

	private long tableUpdatePeriod = 60 * 1000; // 表格更新周期
	private long chartUpdatePeriod = 3 * 1000; // 图表更新周期

	private long tableTotalSize = 0;
	private long chartTotalSize = 0;
	private long preInSize = 0;
	private long preOutSize = 0;
	private Date tableDate = new Date();
	private Date beginDate = new Date();

	private long maxSize = 0; // 流量峰值，单位秒

	private Counter counter = Counter.newInstance();
	private static LongTimeMonitorFrame parent;

	public static LongTimeUpdateData newInstance(LongTimeMonitorFrame p) {
		parent = p;
		return ltud;
	}

	private LongTimeUpdateData() {
		Timer tableTimer = new Timer();
		tableTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				updateTable();
			}
		}, 0, tableUpdatePeriod);

		Timer chartTimer = new Timer();
		chartTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				updateChart();
				updateStatePanel();
			}
		}, 0, chartUpdatePeriod);
	}

	private void updateTable() {
		Date tempDate = new Date();
		// 加以判断，否则可能出现被除数等于零的情况
		if (tempDate.getTime() != tableDate.getTime()) {
			long avgSize = (counter.getTotalSize() - tableTotalSize) * 1000
					/ (tempDate.getTime() - tableDate.getTime());
			parent.updateTable(FormatTime.formatTimeHm(tableDate) + "--" + FormatTime.formatTimeHm(tempDate),
					Tool.formatByteSize(counter.getTotalSize() - tableTotalSize), Tool.formatByteSize(avgSize),
					Tool.formatByteSize(counter.getInSize() - preInSize),
					Tool.formatByteSize(counter.getOutSize() - preOutSize));
			tableTotalSize = counter.getTotalSize();
			tableDate = tempDate;
			preInSize = counter.getInSize();
			preOutSize = counter.getOutSize();
		}
	}

	private void updateChart() {
		parent.updateChart(FormatTime.formatTimeHm(new Date()), counter.getTotalSize() - chartTotalSize);
		long tempSize = (counter.getTotalSize() - chartTotalSize) * 1000 / chartUpdatePeriod;
		if (maxSize < tempSize) {
			maxSize = tempSize;
		}
		chartTotalSize = counter.getTotalSize();
	}

	private void updateStatePanel() {
		Date tempDate = new Date();
		long totalTime = (tempDate.getTime() - beginDate.getTime()) / 1000;
		// 加以判断，否则可能出现被除数等于零的情况
		if (totalTime > 0) {
			long avgSize = counter.getTotalSize() / totalTime;
			parent.updateStatePanel(Tool.formatByteSize(maxSize), Tool.formatByteSize(avgSize),
					Tool.formatByteSize(counter.getTotalSize()), counter.getTotalCount());
		}
	}
}
