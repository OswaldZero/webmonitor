package com.webmonitor.wyh.test;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;

import java.awt.*;

public class TestTimeSeriesChart {

	private static XYDataset createDataset() {
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		Day day = new Day(21, 2, 2018);
		Hour hour22 = new Hour(16, day);
		Hour hour23 = new Hour(27, day);

		TimeSeries timeSeries1 = new TimeSeries("字段一", Minute.class);
		timeSeries1.add(new Minute(25, hour23), 3.80);
		timeSeries1.add(new Minute(22, hour23), 3.59);
		timeSeries1.add(new Minute(32, hour22), 2.38);
		timeSeries1.add(new Minute(14, hour22), 3.35);
		timeSeries1.add(new Minute(18, hour23), 2.34);
		timeSeries1.add(new Minute(57, hour23), 3.31);
		timeSeries1.add(new Minute(14, hour23), 2.29);
		timeSeries1.add(new Minute(23, hour22), 2.25);
		timeSeries1.add(new Minute(46, hour22), 3.16);
		timeSeries1.add(new Minute(40, hour22), 2.16);
		timeSeries1.add(new Minute(6, hour22), 2.95);
		timeSeries1.add(new Minute(51, hour22), 1.93);
		timeSeries1.add(new Minute(2, hour23), 2.86);
		timeSeries1.add(new Minute(6, hour23), 2.84);
		timeSeries1.add(new Minute(9, hour23), 1.79);
		timeSeries1.add(new Minute(29, hour23), 1.72);

		TimeSeries timeSeries2 = new TimeSeries("字段二", Minute.class);
		timeSeries2.add(new Minute(36, hour22), 3.63);
		timeSeries2.add(new Minute(55, hour22), 3.63);
		timeSeries2.add(new Minute(21, hour23), 3.36);
		timeSeries2.add(new Minute(30, hour22), 3.28);
		timeSeries2.add(new Minute(33, hour23), 3.18);
		timeSeries2.add(new Minute(6, hour23), 3.13);
		timeSeries2.add(new Minute(13, hour22), 3.05);
		timeSeries2.add(new Minute(0, hour23), 2.98);
		timeSeries2.add(new Minute(40, hour22), 2.98);
		timeSeries2.add(new Minute(48, hour22), 2.91);
		timeSeries2.add(new Minute(14, hour23), 2.73);
		timeSeries2.add(new Minute(27, hour23), 2.59);
		timeSeries2.add(new Minute(38, hour23), 2.37);

		TimeSeries timeSeries3 = new TimeSeries("字段三", Minute.class);
		timeSeries3.add(new Minute(8, hour23), 2.84);
		timeSeries3.add(new Minute(29, hour22), 2.51);
		timeSeries3.add(new Minute(56, hour22), 2.23);
		timeSeries3.add(new Minute(5, hour23), 2.17);
		timeSeries3.add(new Minute(47, hour22), 2.10);
		timeSeries3.add(new Minute(17, hour22), 1.86);
		timeSeries3.add(new Minute(24, hour23), 1.84);
		timeSeries3.add(new Minute(5, hour22), 1.84);
		timeSeries3.add(new Minute(15, hour23), 1.73);
		timeSeries3.add(new Minute(36, hour22), 1.46);

		dataset.addSeries(timeSeries1);
		dataset.addSeries(timeSeries2);
		dataset.addSeries(timeSeries3);
		return dataset;
	}

	public static void createTimeSeriesChart() {
		JFreeChart timeSeriesChart = ChartFactory.createTimeSeriesChart(
				"动态时序图", "横坐标", "纵坐标", createDataset(), true,
				true, false);
		timeSeriesChart.setBackgroundPaint(Color.white);
		XYPlot plot = timeSeriesChart.getXYPlot();
		setXYPolt(plot);

		ChartFrame frame = new ChartFrame("TestPieChart", timeSeriesChart);
		frame.pack();
		frame.setVisible(true);
	}

	public static void setXYPolt(XYPlot plot) {
		plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(false);
		}
	}

	public static void main(String[] args) {
		createTimeSeriesChart();
	}

}

