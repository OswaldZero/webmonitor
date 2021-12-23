package com.webmonitor.wyh.test;

import javax.swing.*;
import java.awt.*;

public class test11 {
	public static void main(String[] args) {
		JFrame frame = new JFrame("Crystal");
		Container container = frame.getContentPane();// 得到窗体容器
		JSplitPane lpane = null;// 左右分割
		JSplitPane tpane = null;// 上下分割
		lpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JPanel(),
				new JPanel());
		lpane.setDividerSize(3);// 设置左右的分割线的大小
		tpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, lpane, new JPanel());
		tpane.setDividerSize(10);
		tpane.setOneTouchExpandable(true);// 快速展开折叠的分割条
		container.add(tpane);// 将JSpiltPane加入到窗体
		frame.setSize(230, 80);
		frame.setLocation(300, 200);
		frame.setVisible(true);
	}
}