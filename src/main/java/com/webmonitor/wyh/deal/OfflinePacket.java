package com.webmonitor.wyh.deal;


import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

import java.util.concurrent.BlockingQueue;

public class OfflinePacket implements Runnable{
	
	private static OfflinePacket offlinePacket = new OfflinePacket();
	
	private StringBuilder errbuf = new StringBuilder();

	private Pcap pcap = null;
	
	private BlockingQueue<PcapPacket> packetQueue;

	private int index = -1;	//用于获取指定index的数据包
	
	private PcapPacket choosedPacket;
	
	private OfflinePacket() {}
	
	public static OfflinePacket newInstance() {
		return offlinePacket;
	}
	
	public PcapPacket getofflinePacket(String filepath){
		//离线文件抓取数据包
		
		/***************************************************************************
		 * 首先创建一个用来表示错误信息的字符串，和文件名字符串
		 **************************************************************************/

		System.out.printf("Opening file for reading: %s%n", filepath);

		/***************************************************************************
		 * 用openOffline()方法打开选中的文件
		 **************************************************************************/
		pcap = Pcap.openOffline(filepath, errbuf);

		if (pcap == null) {
			System.err.printf("Error while opening device for capture: " + errbuf.toString());
			return null;
		}

		/***************************************************************************
		 * 创建用来接收数据包handler
		 **************************************************************************/
		int i=0;
		PcapPacketHandler<String> jpacketHandler = new PcapPacketHandler<String>() {

			public void nextPacket(PcapPacket packet, String user) {

//				System.out.printf("Received at %s caplen=%-4d len=%-4d %s\n",
//						new Date(packet.getCaptureHeader().timestampInMillis()), packet.getCaptureHeader().caplen(), // Length
//																														// actually
//																														// captured
//						packet.getCaptureHeader().wirelen(), // Original length
//						user // User supplied object
//				);
				if(null!=packetQueue) {
					packetQueue.offer(packet);
				}
				
				if(index!=-1) {
					if(i == index) {
						choosedPacket = packet;
						return ;
					}
				}
			}
		};

		try {
			pcap.loop(Integer.MAX_VALUE, jpacketHandler, "jNetPcap rocks!");
		} finally {
			/***************************************************************************
			 * 最后要关闭pcap
			 **************************************************************************/
			pcap.close();
		}
		return null;
	}

	@Override
	public void run() {
		
	}
	
	
	public PcapPacket getChoosedPacketInFile(String filepath,int index) {
		this.index = index;
		getofflinePacket(filepath);
		this.index = -1;
		return choosedPacket;
	}
	
	public BlockingQueue<PcapPacket> getPacketQueue() {
		return packetQueue;
	}

	public void setPacketQueue(BlockingQueue<PcapPacket> packetQueue) {
		this.packetQueue = packetQueue;
	}

	
}
