package com.webmonitor.wyh.statistics;


import com.webmonitor.wyh.deal.PacketParse;
import com.webmonitor.wyh.utils.Tool;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.JProtocol;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Arp;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.network.Ip6;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;


import java.util.HashMap;
import java.util.Map;

public class ProtocolAnalyse extends Statistics{

	// 单例模式
	private static ProtocolAnalyse ps = new ProtocolAnalyse();

	// 协议类型,JHeader
	private Ethernet eth = new Ethernet();
	private Ip4 ip4 = new Ip4();
	private Ip6 ip6 = new Ip6();
	private Tcp tcp = new Tcp();
	private Udp udp = new Udp();
	private Http http = new Http();
	private Arp arp = new Arp();
	private Icmp icmp = new Icmp();
	// 各个协议所有地址统计 包数量统计
	private Map<String, Integer> ethCountMap = new HashMap<String, Integer>();
	private Map<String, Integer> ip4CountMap = new HashMap<String, Integer>();
	private Map<String, Integer> ip6CountMap = new HashMap<String, Integer>();
	private Map<String, Integer> tcpCountMap = new HashMap<String, Integer>();
	private Map<String, Integer> udpCountMap = new HashMap<String, Integer>();

	// 各个协议所有地址统计 流量统计
	private Map<String, Integer> ethSizeMap = new HashMap<String, Integer>();
	private Map<String, Integer> ip4SizeMap = new HashMap<String, Integer>();
	private Map<String, Integer> ip6SizeMap = new HashMap<String, Integer>();
	private Map<String, Integer> tcpSizeMap = new HashMap<String, Integer>();
	private Map<String, Integer> udpSizeMap = new HashMap<String, Integer>();

	// 提示信息
	private String tipInfo;


	private EthAnalyse ethAna = EthAnalyse.newInstance();
	private ArpAnalyse arpAna = ArpAnalyse.newInstance();
	private IcmpAnalyse icmpAna = IcmpAnalyse.newInstance();
	private Ipv6Analyse ipv6Ana = Ipv6Analyse.newInstance();
	private TcpAnalyse tcpAna = TcpAnalyse.newInstance();
	private UdpAnalyse udpAna = UdpAnalyse.newInstance();
	private HttpAnalyse httpAna = HttpAnalyse.newInstance();
	private IntranetIPAnalyse inipAna = IntranetIPAnalyse.newInstance(this);
	private String protocolName;
	private String src;
	private String dest;
	private int srcport=0;
	private int destport=0;
	
	private int type=0;		//流量类型，IN or OUT
	
	public final static int IN = 0;		//内网流量和向公网发送的流量
	public final static int OUT = 1;	//公网向内网发送的流量

	private ProtocolAnalyse() {
		super();
		registerProtocol();
	}

	public static ProtocolAnalyse newInstance() {
		return ps;
	}

	//重置一些重复使用的数据
	private void resetSomeReuseDate() {
		tipInfo = "";
		protocolName = "";
		type = 0;
		srcport = 0;
		destport = 0;
	}
	
	public String analyse(PcapPacket packet) {
		//重置一些重复使用的数据
		resetSomeReuseDate();
		
		if (packet.hasHeader(eth)) {
			//protocolName = "ETHERNET";
			src = FormatUtils.mac(eth.source());
			dest = FormatUtils.mac(eth.destination());
			put(ethCountMap, src + "-->" + dest, 1);
			put(ethSizeMap, src + "-->" + dest,packet.getPacketWirelen());

			protocolName = ethAna.analyse(eth);

			//判断ARP
			if(packet.hasHeader(arp)) {
				tipInfo = arpAna.analyse(arp);
				colorIndex = arpAna.getColorIndex();
			}
		}
		
		//判断ICMP
		if(packet.hasHeader(icmp)) {
			tipInfo = icmpAna.analyse(icmp);
			colorIndex = icmpAna.getColorIndex();
		}
		
		if (packet.hasHeader(ip4)) {
			//protocolName = "IPv4";
			protocolName = ip4.typeEnum().name();
			src = FormatUtils.ip(ip4.source());
			dest = FormatUtils.ip(ip4.destination());
			//判断流量类型，IN or OUT
			judgeType(ip4.destination());
			put(ip4CountMap, src + "-->" + dest, 1);
			put(ip4SizeMap, src + "-->" + dest,packet.getPacketWirelen());

			//分析内网IP4流量
			inipAna.analyse(ip4.source(),ip4.destination(),packet);

			analyseTransportLayer(packet);

			if (packet.hasHeader(http)) {
				//protocolName = "HTTP";
				tipInfo = httpAna.analyse(http,packet);
				colorIndex = httpAna.getColorIndex();
			}
		}
		if (packet.hasHeader(ip6)) {
			//protocolName = "IPv6";
			src = Tool.parseFullIPv6ToAbbreviation(FormatUtils.ip(ip6.source()));
			dest = Tool.parseFullIPv6ToAbbreviation(FormatUtils.ip(ip6.destination()));
			put(ip6CountMap, src + "-->" + dest, 1);
			put(ip6SizeMap, src + "-->" + dest, packet.getPacketWirelen());

			protocolName = ipv6Ana.analyse(ip6);

			analyseTransportLayer(packet);

			if (packet.hasHeader(http)) {
				tipInfo = httpAna.analyse(http,packet);
				colorIndex = httpAna.getColorIndex();
			}
			
		}

		//解析器无法解析时使用默认解析器
		if( null == protocolName || protocolName.equals("") ) {
			protocolName = PacketParse.parseProtocol(packet);
		}
		return tipInfo;

	}

	private void analyseTransportLayer(PcapPacket packet){
		if (packet.hasHeader(tcp)) {
			//protocolName = "TCP";
			srcport = tcp.source();
			destport = tcp.destination();
			String identify = src + "-->" + dest + "###"
					+ srcport + "-->" + destport;

			tcpAna.addPacketToFlows(packet,identify);

			put(tcpCountMap, identify, 1);
			put(tcpSizeMap, identify, packet.getPacketWirelen());
			tipInfo = tcpAna.anaylse(tcp,packet);
			protocolName = tcpAna.getProtocolName();
			colorIndex = tcpAna.getColorIndex();
		}
		if (packet.hasHeader(udp)) {
			//protocolName = "UDP";
			srcport = udp.source();
			destport = udp.destination();
			String identify = src + "-->" + dest + "###"
					+ srcport + "-->" + destport;
			put(udpCountMap, identify, 1);
			put(udpSizeMap, identify, packet.getPacketWirelen());
			tipInfo = udpAna.analyse(udp);
			protocolName = udpAna.getProtocolName();
			colorIndex = udpAna.getColorIndex();
		}
	}

	private void judgeType(byte[] addr) {
		if(Tool.isPublicIp(addr)) {
			type = OUT;
		}
	}

	//获取提示信息
	public String getTipInfo() {
		return tipInfo;
	}
	
	public int getType() {
		return type;
	}

	// 将加入元素map
	private void put(Map<String, Integer> map, String key, int value) {
		if (map.containsKey(key)) {
			map.put(key, (map.get(key) + value));
		} else {
			map.put(key, value);
		}
	}

	public Map<String, Integer> getEthCountMap() {
		return ethCountMap;
	}

	public Map<String, Integer> getIp4CountMap() {
		return ip4CountMap;
	}

	public Map<String, Integer> getIp6CountMap() {
		return ip6CountMap;
	}

	public Map<String, Integer> getTcpCountMap() {
		return tcpCountMap;
	}

	public Map<String, Integer> getUdpCountMap() {
		return udpCountMap;
	}

	public Map<String, Integer> getEthSizeMap() {
		return ethSizeMap;
	}

	public Map<String, Integer> getIp4SizeMap() {
		return ip4SizeMap;
	}

	public Map<String, Integer> getIp6SizeMap() {
		return ip6SizeMap;
	}

	public Map<String, Integer> getTcpSizeMap() {
		return tcpSizeMap;
	}

	public Map<String, Integer> getUdpSizeMap() {
		return udpSizeMap;
	}

	public String getProtocolName() {
		return protocolName;
	}

	public String getSrc() {
		return src;
	}

	public String getDest() {
		return dest;
	}

	@Override
	public void clean() {
		ethCountMap.clear();
		ip4CountMap.clear();
		ip6CountMap.clear();
		tcpCountMap.clear();
		udpCountMap.clear();
		
		ethSizeMap.clear();
		ip4SizeMap.clear();
		ip6SizeMap.clear();
		tcpSizeMap.clear();
		udpSizeMap.clear();
	}
	
	private void registerProtocol() {
		ProtocolManager.registerProtocol("ETHERNET");
		ProtocolManager.registerProtocol("ARP");
		ProtocolManager.registerProtocol("ICMP");
		ProtocolManager.registerProtocol("IP4");
		ProtocolManager.registerProtocol("IP6");
		ProtocolManager.registerProtocol("TCP");
		ProtocolManager.registerProtocol("UDP");
		ProtocolManager.registerProtocol("HTTP");
		//注册默认协议
		for (JProtocol protocol : JProtocol.values()) {
			ProtocolManager.registerProtocol(protocol.name());
		}
	}

}
