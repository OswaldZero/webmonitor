package com.webmonitor.wyh.statistics;

import com.webmonitor.wyh.protocol.DNS;
import org.jnetpcap.protocol.tcpip.Udp;


public class UdpAnalyse extends Statistics{

	private static UdpAnalyse ua = new UdpAnalyse();
	
	private String protocolName;
	
	private UdpAnalyse() {
		super();
		registerProtocol();
	}
	
	public static UdpAnalyse newInstance() {
		return ua;
	}
	
	public String analyse(Udp udp) {
		//protocolName = "UDP";
		colorIndex = 6;
		protocolName="";
		analyseDetailProtocol(udp.source(),udp.destination());
		return udp.source()+"->"+udp.destination()+" Len="+udp.length();
	}
	
	public String getProtocolName() {
		return protocolName;
	}
	
	@Override
	public void clean() {
		
	}
	
	private void analyseDetailProtocol(int source, int destination) {
		if(analyseByPort(source)) {
			return;
		}else {
			analyseByPort(destination);
		}
	}

	//根据端口号判断是否为特定协议报文协议
	private boolean analyseByPort(int port) {
		
		switch (port) {
		case 53:
			protocolName = "DNS";
			detailProtocolClass = DNS.class;
			break;
		case 63:
			protocolName = "DHCP";
			break;
		case 69:
			protocolName = "TFTP";
			break;
		case 123:
			protocolName = "NTP";
			break;
		case 137:
			protocolName = "NBNS";
			break;
		case 161:
			protocolName = "SNMP";
			break;
		case 162:
			protocolName = "SNMP-TRAP";
			break;
		case 500:
			protocolName = "IPSec";
			break;
		case 1645:
			protocolName = "RADIUS";
			break;
		case 1646:
			protocolName = "RADIUS";
			break;
		case 1701:
			protocolName = "L2TP";
			break;
		case 1812:
			protocolName = "RADIUS";
			break;
		case 1813:
			protocolName = "RADIUS";
			break;
		case 1900:
			protocolName = "SSDP";
			break;
		case 5355:
			protocolName = "LLMNR";
			break;	
		case 8000:
			protocolName = "OICQ";
			break;
		default:
			return false;
		}
		return true;
	}

	private void registerProtocol() {
		ProtocolManager.registerProtocol("DNS");
		ProtocolManager.registerProtocol("DHCP");
		ProtocolManager.registerProtocol("TFTP");
		ProtocolManager.registerProtocol("NTP");
		ProtocolManager.registerProtocol("NBNS");
		ProtocolManager.registerProtocol("SNMP");
		ProtocolManager.registerProtocol("SNMP-TRAP");
		ProtocolManager.registerProtocol("IPSec");
		ProtocolManager.registerProtocol("L2TP");
		ProtocolManager.registerProtocol("TFTP");
		ProtocolManager.registerProtocol("SSDP");
		ProtocolManager.registerProtocol("LLMNR");
		ProtocolManager.registerProtocol("OICQ");
	}
}
