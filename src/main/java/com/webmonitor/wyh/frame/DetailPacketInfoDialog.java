package com.webmonitor.wyh.frame;

import javax.swing.*;

public class DetailPacketInfoDialog extends JDialog{

	private String detailInfo;
	
	private JScrollPane panel;
	private JTextArea detailInfoArea;

	public DetailPacketInfoDialog(String detailInfo,String protocolName) {
		setTitle("深层协议:"+protocolName);
		this.detailInfo = detailInfo;
		detailInfoArea = new JTextArea(detailInfo);
		panel = new JScrollPane(detailInfoArea);
		add(panel);
		
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(400, 400);
		setVisible(true);
	}
}
