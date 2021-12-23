package com.webmonitor.wyh.statistics;


import com.webmonitor.wyh.bean.TcpFlow;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.tcpip.Tcp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TcpAnalyse extends Statistics {

	private static TcpAnalyse ta = new TcpAnalyse();

	private String protocolName;

	private Map<String, TcpFlow> tcpFlows = new HashMap<String,TcpFlow>();

	private TcpAnalyse() {
		super();
		registerProtocol();
	}

	public static TcpAnalyse newInstance() {
		return ta;
	}

	public String anaylse(Tcp tcp, JPacket packet) {
		// protocolName = "TCP";
		protocolName = "";
		colorIndex = 5;
		StringBuffer tipInfo = new StringBuffer();

		// 检查报文是否有错误
		checkErrors(tipInfo, tcp);

		tipInfo.append(tcp.source() + "->" + tcp.destination() + "   ");
		analyseFlags(tcp, tipInfo);
		tipInfo.append("Seq=" + tcp.seq() + "   "); // seq序号
		if (tcp.ack() != 0) {
			tipInfo.append("Ack=" + tcp.ack() + "   "); // ack序号
		}
		tipInfo.append("Win=" + tcp.window() + "   "); // 滑动窗口大小
		tipInfo.append("Len=" + tcp.getPayloadLength() + "   ");
		analyseDetailProtocol(tcp.source(), tcp.destination());
		return tipInfo.toString();
	}

	private void checkErrors(StringBuffer tipInfo, Tcp tcp) {
		if(!tcp.isChecksumValid()) {
			tipInfo.append("[ TCP "+tcp.checksumDescription()+"]   ");
			colorIndex = 0;//tcp bad,hsrp change,stp change,ospf change,icmp error,checksum error
		}
	}

	// URG，ACK,PSH,RST,SYN,FIN
	private void analyseFlags(Tcp tcp, StringBuffer tipInfo) {
		tipInfo.append("[");
		if (tcp.flags_FIN()) {
			tipInfo.append("FIN,");
			if(colorIndex>4)	colorIndex = 4;
		}
		if (tcp.flags_SYN()) {
			tipInfo.append("SYN,");
			if(colorIndex>4)	colorIndex = 4;
		}
		if (tcp.flags_RST()) {
			tipInfo.append("RST,");
			if(colorIndex>3)	colorIndex = 3;
		}
		if (tcp.flags_PSH()) {
			tipInfo.append("PUSH,");
		}
		if (tcp.flags_ACK()) {
			tipInfo.append("ACK,");
		}
		if (tcp.flags_URG()) {
			tipInfo.append("URG,");
		}
		//删除最后一个逗号
		tipInfo.deleteCharAt(tipInfo.length() - 1);
		tipInfo.append("]  ");
	}

	public String getProtocolName() {
		return protocolName;
	}

	@Override
	public void clean() {

	}
	
	private void analyseDetailProtocol(int source, int destination) {
		if (analyseByPort(source)) {
			return;
		} else {
			analyseByPort(destination);
		}
	}

	//向tcpFlows添加数据
	public void addPacketToFlows(PcapPacket packet,String identify){

		if(!tcpFlows.containsKey(identify)){
			TcpFlow tcpFlow = new TcpFlow();
			tcpFlow.setIdentify(identify);
			tcpFlow.setFirstFrameId(packet.getFrameNumber());
			List<Long> pkts = new ArrayList<>();
			pkts.add(packet.getFrameNumber());
			tcpFlow.setPkts(pkts);
			tcpFlows.put(identify,tcpFlow);
		}else{
			TcpFlow tcpFlow = tcpFlows.get(identify);
			List<Long> pkts = tcpFlow.getPkts();
			pkts.add(packet.getFrameNumber());
		}
		if(tcpFlows.size()==8){
			System.out.println(tcpFlows);
		}
	}

	// 根据端口号判断是否为特定协议报文协议
	private boolean analyseByPort(int port) {
		switch (port) {
		case 20:
			protocolName = "FTP";
			break;
		case 21:
			protocolName = "FTP";
			break;
		case 22:
			protocolName = "SSH";
			break;
		case 23:
			protocolName = "TELNET";
			break;
		case 25:
			protocolName = "SMTP";
			break;
		case 110:
			protocolName = "POP3";
			break;
		case 143:
			protocolName = "IMAP";
			break;
		case 443:
			protocolName = "HTTPS";
			break;
		case 1723:
			protocolName = "PPTP";
			break;
		case 3389:
			protocolName = "TS";	//Terminal Services
			break;
		default:
			return false;
		}
		// if(protocolName!=""){
		// System.out.println(protocolName);
		// }
		return true;
	}
	
	private void registerProtocol() {
		ProtocolManager.registerProtocol("FTP");
		ProtocolManager.registerProtocol("SSH");
		ProtocolManager.registerProtocol("FTP");
		ProtocolManager.registerProtocol("TELNET");
		ProtocolManager.registerProtocol("SMTP");
		ProtocolManager.registerProtocol("POP3");
		ProtocolManager.registerProtocol("IMAP");
		ProtocolManager.registerProtocol("HTTPS");
		ProtocolManager.registerProtocol("PPTP");
		ProtocolManager.registerProtocol("TS");
	}

}
