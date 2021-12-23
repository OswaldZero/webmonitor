package com.webmonitor.wyh.frame;



import com.webmonitor.wyh.bean.IntranetIP;
import com.webmonitor.wyh.statistics.IntranetIPAnalyse;
import com.webmonitor.wyh.utils.Tool;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Map;

public class IntranetIPStatisticsFrame extends JFrame {

	private IntranetIPAnalyse inipAna;

	private JScrollPane pane;
	private JTable table;
	private DefaultTableModel tableModel;
	private static final String[] header = { "Address", "Packets", "pRate", "TotalBytes", "bRate", "InBytes",
			"OutBytes" };
	private final static float[] tableColumnWidthPercentage = { 40.0f, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f };

	private Map<String, IntranetIP> ipMap;

	private long totalCount = 0;
	private long totalSize = 0;

	public IntranetIPStatisticsFrame() {
		inipAna = IntranetIPAnalyse.newInstance();
		ipMap = inipAna.getIpMap();
		setTitle("内网IP流量分析  " + ipMap.size() + "个用户");
		initComponents();
		addListener();
		statisticsData();
		loadAndShowData();
	}

	private void initComponents() {
		tableModel = new DefaultTableModel(new Object[0][0], header);
		table = new JTable(tableModel);
		table.setRowSorter(new TableRowSorter<DefaultTableModel>(tableModel));
		pane = new JScrollPane(table);
		add(pane);

		setSize(600, 600);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	private void addListener() {
		// 设置表格各列宽度占比 tableColumnWidthPercentage
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int tW = table.getWidth();
				TableColumnModel jTableColumnModel = table.getColumnModel();
				int cantCols = jTableColumnModel.getColumnCount();
				TableColumn column;
				for (int i = 0; i < cantCols; i++) {
					column = jTableColumnModel.getColumn(i);
					int pWidth = Math.round(tableColumnWidthPercentage[i] * tW);
					column.setPreferredWidth(pWidth);
				}
			}
		});
	}
	
	private void statisticsData() {
		for (IntranetIP inip : ipMap.values()) {
			totalCount += inip.getCount();
			totalSize += inip.getSize();
		}
	}

	private void loadAndShowData() {
		for (IntranetIP inip : ipMap.values()) {
			tableModel.addRow(new Object[] { inip.getIp(), inip.getCount(),
					Tool.transFloatToHundred(((float) inip.getCount()) / totalCount), inip.getSize(),
					Tool.transFloatToHundred(((float) inip.getSize()) / totalSize), inip.getInSize(),
					inip.getOutSize() });
		}
	}
}
