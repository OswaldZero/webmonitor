package com.webmonitor.wyh.statistics;

import org.jnetpcap.protocol.network.Icmp;

public class IcmpAnalyse extends Statistics{

	private static IcmpAnalyse icmpAna = new IcmpAnalyse();
	
	private IcmpAnalyse() {
		super();
	}
	
	public static IcmpAnalyse newInstance() {
		return icmpAna;
	}
	
	public String analyse(Icmp icmp) {
		colorIndex = 1;
		return icmp.typeDescription()+"---checkSum:"+icmp.checksumDescription(); 
	}
	
	
	@Override
	public void clean() {
		
	}

}
