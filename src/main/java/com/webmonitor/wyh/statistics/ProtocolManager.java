package com.webmonitor.wyh.statistics;

import java.util.ArrayList;
import java.util.List;

public class ProtocolManager {

	public static List<String> protocolNames = new ArrayList<String>();
	
	public static void registerProtocol(String protocolName){
		protocolNames.add(protocolName);
	}
}
