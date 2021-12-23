package com.webmonitor.wyh.statistics;

import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Arp;
import org.jnetpcap.protocol.network.Arp.OpCode;

import java.util.HashMap;
import java.util.Map;

public class ArpAnalyse extends Statistics {

	private static ArpAnalyse arpAna = new ArpAnalyse();
	
	//key ip -- value mac
	private Map<String, String> ipMacMapping = new HashMap<String, String>();

	private ArpAnalyse() {
		super();
	}

	public static ArpAnalyse newInstance() {
		return arpAna;
	}

	public String analyse(Arp arp) {
		colorIndex = 1;
		if(arp.operationEnum() == OpCode.REQUEST) {
			return "[" + arp.operationDescription() + "]" + " Who has " + FormatUtils.ip(arp.spa()) + "? Tell"
					+ FormatUtils.ip(arp.tpa());
		}else if(arp.operationEnum() == OpCode.REPLY) {
			ipMacMapping.put(FormatUtils.ip(arp.tpa()),FormatUtils.mac(arp.tha()));
			return "[" + arp.operationDescription() + "]"+FormatUtils.ip(arp.tpa())+" is at "+FormatUtils.mac(arp.tha());
		}
		return "[" + arp.operationDescription() + "]";
		
	}
	
	public Map<String, String> getIpMacMapping() {
		return ipMacMapping;
	}

	@Override
	public void clean() {

	}

}
