package com.webmonitor.wyh.statistics;

import com.webmonitor.wyh.protocol.Protocol;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;


public class DetailProtocolAnalyse {

	private static DetailProtocolAnalyse dpa = new DetailProtocolAnalyse();

	private Udp udp = new Udp();
	private Tcp tcp = new Tcp();

	private UdpAnalyse udpAna = UdpAnalyse.newInstance();
	private TcpAnalyse tcpAna = TcpAnalyse.newInstance();
	private Class<? extends Protocol> protocol = null;

	private int beginOffset = 0;
	private int payloadLength = 0;
	private byte[] payload;
	
	private DetailProtocolAnalyse() {
	}

	public static DetailProtocolAnalyse newInstance() {
		return dpa;
	}

	//重置一些重复使用的数据
	private void resetSomeReuseDate() {
		beginOffset = 0;
		payloadLength = 0;
		protocol = null;
		tcpAna.setDetailProtocolClass(null);
		udpAna.setDetailProtocolClass(null);
	}
	
	public String analyse(JPacket packet){
		resetSomeReuseDate();
		
		if(packet.hasHeader(tcp)){
			tcpAna.anaylse(tcp,packet);
			beginOffset = tcp.getPayloadOffset();
			payloadLength = tcp.getPayloadLength();
			payload = tcp.getPayload();
		}else if(packet.hasHeader(udp)){
			udpAna.analyse(udp);
			beginOffset = udp.getPayloadOffset();
			payloadLength = udp.getPayloadLength();
			payload = udp.getPayload();
		}
		if(null!=tcpAna.getDetailProtocolClass()) {
			protocol = tcpAna.getDetailProtocolClass();
		}else if(null!=udpAna.getDetailProtocolClass()) {
			protocol = udpAna.getDetailProtocolClass();
		}
		if(protocol!=null) {
			Protocol p;
			try {
				p = protocol.newInstance();
				return analyseDetail(p,payload);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			
		}
		return "";
	}

	public String getProtocolName() {
		return (null==protocol)?"":protocol.getSimpleName();
	}
	
	private String analyseDetail(Protocol protocol, byte[] payload) {
		protocol.setPayload(payload);
		return protocol.toString();
	}
}

