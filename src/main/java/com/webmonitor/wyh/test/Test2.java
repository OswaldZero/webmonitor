package com.webmonitor.wyh.test;

import com.webmonitor.wyh.statistics.DetailProtocolAnalyse;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.JMemoryPacket;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.protocol.JProtocol;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

import java.util.ArrayList;
import java.util.List;

public class Test2 {

	public static void main(String[] args) {

		// 创建一个TCP数据包

		// JPacket packet = new JMemoryPacket(JProtocol.ETHERNET_ID,
		// " 001801bf 6adc0025 4bb7afec 08004500 "
		// +" 0041a983 40004006 d69ac0a8 00342f8c "
		// + " ca30c3ef 008f2e80 11f52ea8 4b578018 "
		// + " ffffa6ea 00000101 080a152e ef03002a "
		// + " 2c943538 322e3430 204e4f4f 500d0a");

//		JPacket packet = new JMemoryPacket(JProtocol.ETHERNET_ID,
//				"14144b1b555b507b9d1dac2408004500003a5d950000401100000a6cda2972727272dc4c00350026c9b1"
//				+ "71cb8982000100000000000002686d05626169647503636f6d0000010001");
		
		
//		JPacket packet = new JMemoryPacket(JProtocol.ETHERNET_ID,"507b9d1dac2414144b1b555b08004500007e0000000091115ff5727272720a6cda290035cea3006a2c472a388180000100040000000007626561636f6e73046776743203636f6d0000010001c00c00010001000000780004cbd02857c00c00010001000000780004cbd0284fc00c00010001000000780004cbd0285fc00c00010001000000780004cbd02858");
		
		JPacket packet = new JMemoryPacket(JProtocol.ETHERNET_ID,"507b9d1dac2414144b1b555b0800"
				+ "450000f10000000091115f82727272720a6cda29"
				+ "0035ffb800ddcba9"
				+ "c8288180000100080000000007696d673230313807636e626c6f677303636f6d0000010001c00c0005000100000025002007696d673230313807636e626c6f677303636f6d0363646e05646e737631c01cc031000500010000002500180731323934343732037032330274630663646e746970c01cc05d0001000100000020000401c6047ec05d0001000100000020000401c75d70c05d000100010000002000042463e2cac05d0001000100000020000401c75dedc05d0001000100000020000401c60477c05d0001000100000020000401c60448");
		
		Ip4 ip = packet.getHeader(new Ip4());
		Tcp tcp = packet.getHeader(new Tcp());

		// tcp.destination(80);
		//
		// ip.checksum(ip.calculateChecksum());
		// tcp.checksum(tcp.calculateChecksum());
		packet.scan(Ethernet.ID);

		//System.out.println(packet.toHexdump());
		DetailProtocolAnalyse.newInstance().analyse(packet);

        
		// 发送数据包，一次发送一个数据包

		List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs
		StringBuilder errbuf = new StringBuilder(); // For any error msgs

		/***************************************************************************
		 * 获取设备列表
		 **************************************************************************/
		int r = Pcap.findAllDevs(alldevs, errbuf);
		if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
			System.err.printf("Can't read list of devices, error is %s", errbuf.toString());
			return;
		}
		PcapIf device = alldevs.get(1); // We know we have atleast 1 device

		/*****************************************
		 * 打开网络接口
		 *****************************************/
		int snaplen = 64 * 1024; // Capture all packets, no trucation
		int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
		int timeout = 10 * 1000; // 10 seconds in millis
		Pcap pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);

		/*******************************************************
		 * 再一个open状态接口发送这个packet
		 *******************************************************/
		if (pcap.sendPacket(packet) != Pcap.OK) {
			System.err.println(pcap.getErr());
		}

		/********************************************************
		 * 最后关闭
		 ********************************************************/
		pcap.close();

	}

}
