package com.webmonitor.wyh.test;

import com.webmonitor.wyh.statistics.ProtocolAnalyse;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.network.Ip4;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Test implements Runnable{
	
//	public Test() {
//		Thread t = new Thread(this);
//		t.start();
//	}
	
	public static long dataLength=0;
	BlockingQueue<PcapPacket> packetQueue = new LinkedBlockingQueue<>();
	List<PcapPacket> pcapList = new LinkedList<>();
	
	public void catchPacket() {
		
		
		// 获取当前机器的网卡信息
		StringBuilder errbuf = new StringBuilder();

		List<PcapIf> alldevs = new ArrayList<PcapIf>();

		int r = Pcap.findAllDevs(alldevs, errbuf);
		if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
			System.err.printf("Can't read list of devices, error is %s", errbuf.toString());
			return;
		}

		// 输出网卡信息
		System.out.println("Network devices found:");

		int i = 0;
		for (PcapIf device : alldevs) {
			System.out.printf("#%d: %s [%s]\n", i++, device.getName(), device.getDescription());
		}

		// 选择要监控的网卡
		PcapIf device = alldevs.get(1);

		// 打开设备
		int snaplen = 64 * 1024;
		int flags = Pcap.MODE_PROMISCUOUS;// 混杂模式 接受所有经过网卡的帧
		int timeout = 10 * 1000;
		Pcap pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);

		if (pcap == null) {
			System.err.printf("Error while opening device for capture: " + errbuf.toString());
			return;
		}
		
		Ip4 ip = new Ip4();

		// 创建一个数据包处理器
		PcapPacketHandler<String> jpacketHandler = new PcapPacketHandler<String>() {

			public void nextPacket(PcapPacket packet, String user) {

//				System.out.printf("Received at %s caplen=%-4d len=%-4d %s\n",
//						new Date(packet.getCaptureHeader().timestampInMillis()), packet.getCaptureHeader().caplen(), // Length
//																														// actually
//																														// captured
//						packet.getCaptureHeader().wirelen(), // Original length
//						user // User supplied object
//				);
//				dataLength += packet.getCaptureHeader().wirelen();
//				System.out.println("sumDataLength="+dataLength+"byte");
//				if(packet.hasHeader(ip)) {
//					String str = FormatUtils.ip(ip.destination());
//					System.out.printf("#%d: ip.src=%s\n", packet.getFrameNumber(), str);
//				}
				//packetQueue.offer(packet);
				pcapList.add(packet);
				if(pcapList.size()%2000==0) {
					System.out.println(pcapList.size());
				}
				ProtocolAnalyse ps = ProtocolAnalyse.newInstance();
				ps.analyse(packet);
				if(ps.getTipInfo()!="") {
					System.out.println(ps.getTipInfo());
				}
			}
		};
		
		
		// 循环监听
		// 可以修改 10 为 Integer.MAX_VALUE 来长期监听
		pcap.loop(Integer.MAX_VALUE, jpacketHandler, "jNetPcap rocks!");
		// 监听结束后关闭
		pcap.close();

	}

	@Override
	public void run() {
		while(true) {
			while(packetQueue.size()>0) {
				try {
					packetQueue.take();
				} catch (InterruptedException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		Test test = new Test();
		test.catchPacket();
	}
}
