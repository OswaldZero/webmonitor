package com.webmonitor.wyh.frame;


import com.webmonitor.wyh.deal.CatchPacket;
import com.webmonitor.wyh.deal.PacketTransport;
import com.webmonitor.wyh.thread.ThreadManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class DeviceInfoDialog extends JDialog implements ActionListener {

	private List<String> allDeviceInfo = null;
	private JPanel dialogPanel = null;
	private JPanel[] devicePanel = null;
	private JLabel[] deviceLabel = null;
	private JButton[] deviceButton = null;
	private CatchPacket deal = null;
	private MainPage parent = null; // 消息框的父窗口对象
	private PacketTransport pktTrans = null;
	private JCheckBox cb_filter; // 是否使用过滤器

	public DeviceInfoDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		parent = (MainPage) owner;
		
		// setTitle("选择要监控的网卡");
		// setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		showDeviceInfo();
		setSize(800, 200 + 50 * allDeviceInfo.size());
		setVisible(true);
		addOnClickListener();
	}

	private void addOnClickListener() {
		for (int i = 0; i < deviceButton.length; i++) {
			deviceButton[i].addActionListener(this);
		}
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
//				setVisible(false);
//				dispose();
				showMessage("请先选择网卡！");
			}

		});
	}

	// 显示设备信息
	private void showDeviceInfo() {
		getDeviceInfo();
		dialogPanel = new JPanel();
		add(dialogPanel);
		devicePanel = new JPanel[allDeviceInfo.size()];
		deviceLabel = new JLabel[allDeviceInfo.size()];
		deviceButton = new JButton[allDeviceInfo.size()];
		for (int i = 0; i < allDeviceInfo.size(); i++) {
			devicePanel[i] = new JPanel();
			deviceLabel[i] = new JLabel(allDeviceInfo.get(i));
			deviceButton[i] = new JButton("选择");
			devicePanel[i].add(deviceLabel[i]);
			devicePanel[i].add(deviceButton[i]);
			dialogPanel.add(devicePanel[i]);
		}
		cb_filter = new JCheckBox("是否使用过滤器");
		dialogPanel.add(cb_filter);
	}

	// 获取设备信息
	private void getDeviceInfo() {
		deal = CatchPacket.newInstance();
		allDeviceInfo = deal.getAllDevice();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for (int i = 0; i < deviceButton.length; i++) {
			if (e.getSource() == deviceButton[i]) {
				this.setVisible(false);
				this.dispose();
				pktTrans = PacketTransport.newInstance();
				pktTrans.setIndex(i);
				//初始化pcap,防止其为空
				pktTrans.initPcap();
				if (!cb_filter.isSelected()) {
					//直接抓包
					
					if(ThreadManager.newInstance().hasThread()) {
						//已经抓过包，再次抓包时
						parent.setSuspend(false);	
						pktTrans.startCatchPacket();
						return ;
					}
					pktTrans.startCatchPacket();
					parent.startShowPackets(pktTrans.getDeviceInfo(pktTrans.getIndex()), pktTrans.getPacketQueue());
				}else {
					//开启过滤器抓包
					new FilterPreCatchDialog(parent);
				}
			}
		}
	}
	
	private void showMessage(String message) {
		JOptionPane.showMessageDialog(this, message);
	}


}
