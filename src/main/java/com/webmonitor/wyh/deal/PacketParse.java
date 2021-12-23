package com.webmonitor.wyh.deal;


import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.JProtocol;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.network.Ip6;


/*
 * 使用 格式化工具吧源数据变为容易看懂的数据
 */
public class PacketParse {

	private static Ip4 ip4 = new Ip4();
	private static Ip6 ip6 = new Ip6();
	private static Ethernet eth = new Ethernet();
	private static String str;
	
	//解析出源地址
	public static String parseSrc(PcapPacket packet) {
		if (parseSrcIP4(packet) != null) {
			return parseSrcIP4(packet);
		}else if(parseSrcIP6(packet) != null) {
			return parseSrcIP6(packet);
		}else if(parseSrcMac(packet) != null) {
			return parseSrcMac(packet);
		}
		return null;
	}
	
	//解析出目的地址
	public static String parseDest(PcapPacket packet) {
		if (parseDestIP4(packet) != null) {
			return parseDestIP4(packet);
		}else if(parseDestIP6(packet) != null) {
			return parseDestIP6(packet);
		}else if(parseDestMac(packet) != null) {
			return parseDestMac(packet);
		}
		return null;
	}
	
	
	//解析出源IP4
	public static String parseSrcIP4(PcapPacket packet) {
		if (packet.hasHeader(ip4)) { // 如果packet有ip头部
			str = FormatUtils.ip(ip4.source());
			return str;
		}
		return null;
	}
	
	//解析出目的IP4
	public static String parseDestIP4(PcapPacket packet) {
		if (packet.hasHeader(ip4)) { // 如果packet有ip头部
			str = FormatUtils.ip(ip4.destination());
			return str;
		}
		return null;
	}
	
	//解析出源IP6
	public static String parseSrcIP6(PcapPacket packet) {
		if (packet.hasHeader(ip6)) { // 如果packet有ip头部
			str = FormatUtils.ip(ip6.source());
			return str;
		}
		return null;
	}
	
	//解析出目的IP6
	public static String parseDestIP6(PcapPacket packet) {
		if (packet.hasHeader(ip6)) { // 如果packet有ip头部
			str = FormatUtils.ip(ip6.destination());
			return str;
		}
		return null;
	}
	
	//解析出源Mac地址
	public static String parseSrcMac(PcapPacket packet) {
		if (packet.hasHeader(eth)) { // 如果packet有eth头部
			str = FormatUtils.mac(eth.source());
			return str;
		}
		return null;
	}
	
	//解析出目的Mac地址
	public static String parseDestMac(PcapPacket packet) {
		if (packet.hasHeader(eth)) { // 如果packet有eth头部
			str = FormatUtils.mac(eth.destination());
			return str;
		}
		return null;
	}
	
	//解析出协议类型
	public static String parseProtocol(PcapPacket packet) {
		
		//逆向遍历协议表找到最精确（最高层）的协议名
		JProtocol[] protocols = JProtocol.values();
		for (int i=protocols.length-1;i>=0;i--) {
			if(packet.hasHeader(protocols[i].getId())) {
				return protocols[i].name();
			}
		}
		return null;
	}

	//解析出协议在JProtocol中的id
	public static int parseProtocolId(PcapPacket packet) {
		
		//逆向遍历协议表找到最精确（最高层）的协议名
		JProtocol[] protocols = JProtocol.values();
		for (int i=protocols.length-1;i>=0;i--) {
			if(packet.hasHeader(protocols[i].getId())) {
				return i;
			}
		}
		return -1;
	}
	
	public static void main(String[] args) {
		JProtocol[] protocols = JProtocol.values();
		for (int i=protocols.length-1;i>=0;i--) {
			System.out.println(protocols[i].name());
		}
		
	}
	
}
