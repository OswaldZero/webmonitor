package com.webmonitor.wyh.frame;



import com.webmonitor.wyh.utils.ColorRule;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class MyTableCellRenderer extends DefaultTableCellRenderer{
	private List<Integer> colorIndex; // 每一行在过滤颜色数组color[]中的序号
	private Color[] colors; // 所有过滤颜色
	private MainPage parent = null;
//	int i=0;

	public MyTableCellRenderer(JTable table, MainPage parent) {
		this.parent = parent;
		int columnCount = table.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			table.getColumn(table.getColumnName(i)).setCellRenderer(this);
		}
		colors = ColorRule.colors;
		colorIndex = parent.getColorIndex();
	}
	
	public MyTableCellRenderer(JTable table,List<Integer> colorIndex) {
		int columnCount = table.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			table.getColumn(table.getColumnName(i)).setCellRenderer(this);
		}
		colors = ColorRule.colors;
		this.colorIndex = colorIndex;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

//		System.out.println(i++);
		if(colorIndex.isEmpty()||colorIndex.get(row)==-1) {
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
		
		//仅在无过滤器模式下使用颜色标记（是由于在显示过滤器模式的出现了界面显示bug）
		if(parent.getShowMode()==MainPage.NOFILTER) {
			setBackground(colors[colorIndex.get(row)]);
		}else {
			//默认颜色
			setBackground(colors[colors.length-1]);
		}
		//setBackground(colors[colorIndex.get((int)(table.getValueAt(row, 0))-1)]);

		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}

}

