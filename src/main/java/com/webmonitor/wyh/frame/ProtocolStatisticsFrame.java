package com.webmonitor.wyh.frame;

import javax.swing.*;

public class ProtocolStatisticsFrame extends JFrame{

	private JTabbedPane tabbedPane;
	private ProtocolStatisticsPanel ethPanel,ip4Panel,ip6Panel,tcpPanel,udpPanel,httpPanel;
	
	public final static int Ethernet = 1;
	public final static int IPv4 = 2;
	public final static int IPv6 = 3;
	public final static int TCP = 4;
	public final static int UDP = 5;
	public final static int HTTP = 6;
	
	public ProtocolStatisticsFrame() {
		setTitle("包数据统计");
		initComponents();
		setSize(600, 600);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
		
	}

	private void initComponents() {
		
		//initPanel
		ethPanel = new ProtocolStatisticsPanel(Ethernet);
		ip4Panel = new ProtocolStatisticsPanel(IPv4);
		ip6Panel = new ProtocolStatisticsPanel(IPv6);
		tcpPanel = new ProtocolStatisticsPanel(TCP);
		udpPanel = new ProtocolStatisticsPanel(UDP);
		httpPanel = new ProtocolStatisticsPanel(HTTP);
		
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Ethernet  "+ethPanel.getTotalCount(), ethPanel);
		tabbedPane.addTab("IPv4  "+ip4Panel.getTotalCount(), ip4Panel);
		tabbedPane.addTab("IPv6  "+ip6Panel.getTotalCount(), ip6Panel);
		tabbedPane.addTab("TCP  "+tcpPanel.getTotalCount(), tcpPanel);
		tabbedPane.addTab("UDP  "+udpPanel.getTotalCount(), udpPanel);
		tabbedPane.addTab("HTTP  "+httpPanel.getTotalCount(), httpPanel);
		
		add(tabbedPane);
	}
	
}
