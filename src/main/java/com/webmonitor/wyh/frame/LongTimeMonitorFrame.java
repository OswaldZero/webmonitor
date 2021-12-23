package com.webmonitor.wyh.frame;


import com.webmonitor.wyh.chart.ChartCreater;
import com.webmonitor.wyh.deal.LongTimeUpdateData;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

public class LongTimeMonitorFrame extends JFrame {

	private JScrollPane mainPane;
	private JPanel mainPanel;

	private ChartPanel chartPanel;
	private JFreeChart chart;
	private TimeSeriesCollection dataset;
	private TimeSeries timeSeries;
	private int maxChartSize = 300;
	private int chartSize = 0;

	private JScrollPane tablePane;
	private JTable realtimeTable;
	private DefaultTableModel tableModel;
	private final static String[] header = { "时间", "流量(byte)", "平均速率", "入流量", "出流量" };

	private LongTimeUpdateData ltud;

	private JPanel statePanel;
	private JLabel maxSizeLabel, avgSizeLabel, totalSizeLabel, totalCountLabel;

	public LongTimeMonitorFrame(MainPage parent) {
		setTitle("长期监控模式:统计");
		initComponent();
		addListener();
		setSize(600, 600);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setVisible(true);
	}

	private void initComponent() {

		// 创建边框
		Border blueBorder = BorderFactory.createMatteBorder(1, 5, 1, 1, Color.BLUE);
		Border yellowBorder = BorderFactory.createMatteBorder(1, 5, 1, 1, Color.YELLOW);
		Border greenBorder = BorderFactory.createMatteBorder(1, 5, 1, 1, Color.GREEN);
		Border chartTitleBorder = BorderFactory.createTitledBorder("实时流量监控图");
		Border tableTitleBorder = BorderFactory.createTitledBorder("历史流量");
		Border stateTitleBorder = BorderFactory.createTitledBorder("状态栏");

		// 创建chart
		createChart();
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(getWidth(), 200));
		chartPanel.setBorder(BorderFactory.createCompoundBorder(blueBorder, chartTitleBorder));
		tableModel = new DefaultTableModel(new Object[0][0], header);
		realtimeTable = new JTable(tableModel);
		tablePane = new JScrollPane(realtimeTable);
		tablePane.setBorder(BorderFactory.createCompoundBorder(yellowBorder, tableTitleBorder));
		statePanel = new JPanel();
		initStatePanel();
		statePanel.setBorder(BorderFactory.createCompoundBorder(greenBorder, stateTitleBorder));
		add(chartPanel, BorderLayout.NORTH);
		add(tablePane, BorderLayout.CENTER);
		add(statePanel, BorderLayout.SOUTH);
		ltud = LongTimeUpdateData.newInstance(this);
	}

	private void initStatePanel() {
		maxSizeLabel = new JLabel("流量峰值:");
		avgSizeLabel = new JLabel("平均流量值:");
		totalSizeLabel = new JLabel("总流量:");
		totalCountLabel = new JLabel("总包数:");
		GridLayout gr = new GridLayout(2, 2);
		gr.setVgap(5);
		gr.setVgap(5);
		statePanel.setLayout(gr);
		statePanel.add(maxSizeLabel);
		statePanel.add(avgSizeLabel);
		statePanel.add(totalSizeLabel);
		statePanel.add(totalCountLabel);
	}

	private void addListener() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// confirmMessage();
				showMessage("请通过切换模式来关闭此窗口");
			}
		});
	}

	private void createChart() {

		timeSeries = new TimeSeries("");
		timeSeries.setMaximumItemCount(maxChartSize);
		dataset = new TimeSeriesCollection(timeSeries);
		chart = ChartCreater.newInstance().createTimeSeriesChart("实时流量", "", "流量（byte）", dataset);
	}

	public void updateTable(String time, String size, String avgSize, String inSize, String outSize) {
		tableModel.addRow(new Object[] { time, size, avgSize+"/s", inSize, outSize});
	}

	public void updateChart(String formatTimeHm, long size) {
		if (chartSize < maxChartSize) {
			chartSize++;
		} else {
			timeSeries.removeAgedItems(true);
		}
		timeSeries.add(new Second(new Date()), size);
	}

	public void updateStatePanel(String maxSize, String avgSize, String totalSize, long totalCount) {
		maxSizeLabel.setText("流量峰值:" + maxSize + "/s");
		avgSizeLabel.setText("平均流量值:" + avgSize + "/s");
		totalSizeLabel.setText("总流量:" + totalSize);
		totalCountLabel.setText("总包数:" + totalCount);
	}

	private void confirmMessage() {
		int i = JOptionPane.showConfirmDialog(this, "关闭窗口将退出长期监控模式\n是否确认关闭？", "warning", JOptionPane.OK_CANCEL_OPTION);
		if (i == JOptionPane.OK_OPTION) {
			setVisible(false);
			dispose();
		}
	}

	private void showMessage(String message) {
		JOptionPane.showMessageDialog(this, message);
	}

}
