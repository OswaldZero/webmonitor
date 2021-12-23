package com.webmonitor.wyh.frame;



import com.webmonitor.wyh.statistics.HttpAnalyse;
import com.webmonitor.wyh.statistics.ProtocolAnalyse;
import com.webmonitor.wyh.utils.Tool;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Map;

public class ProtocolStatisticsPanel extends JScrollPane {

	private JTable table;
	private DefaultTableModel tableModel;
	private static final String[] header = { "Address", "Packets", "pRate", "Bytes" };
	private int tabType;

	private ProtocolAnalyse ptlAna;
	private HttpAnalyse httpAnalyse;

	private Map<String, Integer> countMap;
	private Map<String, Integer> sizeMap;

	private final static float[] tableColumnWidthPercentage = { 70.0f, 10.0f, 10.0f, 10.0f };

	private int sum = 0;

	public ProtocolStatisticsPanel(int type) {
		tabType = type;
		tableModel = new DefaultTableModel(new Object[0][0], header);
		table = new JTable(tableModel);
		table.setRowSorter(new TableRowSorter<DefaultTableModel>(tableModel));
		setViewportView(table);
		loadData();
		// System.out.println("countMap.size="+countMap.size());
		calculateTotalCount();
		showData();
		addListener();
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

	private void loadData() {
		ptlAna = ProtocolAnalyse.newInstance();
		httpAnalyse = HttpAnalyse.newInstance();
		switch (tabType) {
		case ProtocolStatisticsFrame.Ethernet:
			countMap = ptlAna.getEthCountMap();
			sizeMap = ptlAna.getEthSizeMap();
			break;
		case ProtocolStatisticsFrame.IPv4:
			countMap = ptlAna.getIp4CountMap();
			sizeMap = ptlAna.getIp4SizeMap();
			break;
		case ProtocolStatisticsFrame.IPv6:
			countMap = ptlAna.getIp6CountMap();
			sizeMap = ptlAna.getIp6SizeMap();
			break;
		case ProtocolStatisticsFrame.TCP:
			countMap = ptlAna.getTcpCountMap();
			sizeMap = ptlAna.getTcpSizeMap();
			break;
		case ProtocolStatisticsFrame.UDP:
			countMap = ptlAna.getUdpCountMap();
			sizeMap = ptlAna.getUdpSizeMap();
			break;
		case ProtocolStatisticsFrame.HTTP:
			countMap = httpAnalyse.getHostCountMap();
			sizeMap = httpAnalyse.getHostSizeMap();
			break;
		default:
			break;
		}
	}

	// 显示数据
	private void showData() {
		for (String address : countMap.keySet()) {
			tableModel.addRow(new Object[] { address, countMap.get(address),
					Tool.transFloatToHundred(((float) countMap.get(address)) / sum), sizeMap.get(address) });
		}
	}

	// 计算包的总数量
	private void calculateTotalCount() {
		for (Integer i : countMap.values()) {
			sum = sum + i;
		}
	}

	public int getTotalCount() {
		return sum;
	}

}
