package com.webmonitor.wyh.statistics;


import com.webmonitor.wyh.bean.IntranetIP;
import com.webmonitor.wyh.utils.Tool;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.format.FormatUtils;


import java.util.HashMap;
import java.util.Map;

//内网IP流量分析
public class IntranetIPAnalyse extends Statistics {

	// 注：原有考虑，在实时抓包时不进行此分析，以减少实时抓包计算量，待到需要使用此功能时再去分析，实施时发现此想法存在的问题，
	// 如果不实时进行分析，无法获取到具体包的大小，数

	private static IntranetIPAnalyse inipAna = new IntranetIPAnalyse();

	private Map<String, IntranetIP> ipMap = new HashMap<String, IntranetIP>();

	private static ProtocolAnalyse ptlAna;

	private IntranetIP inip;

	private long size;
	
	private IntranetIPAnalyse() {
	}

	public static IntranetIPAnalyse newInstance(ProtocolAnalyse ptlA) {
		ptlAna = ptlA;
		return inipAna;
	}

	public static IntranetIPAnalyse newInstance() {
		return inipAna;
	}

	public void analyse(byte[] srcb, byte[] destb,PcapPacket packet) {
		size = packet.size();
		// 分析源IP地址
		analyseIP(srcb, 0 );
		// 分析目的IP地址
		analyseIP(destb, 1);
	}

	// type 等于0表示源ip,等于1表示目的ip
	private void analyseIP(byte[] ipb, int type) {
		if (!Tool.isPublicIp(ipb)) {
			String ip = FormatUtils.ip(ipb);
			if (!ipMap.containsKey(ip)) {
				inip = new IntranetIP();
				inip.setIp(ip);
				setData(inip, type);
				ipMap.put(ip, inip);
			} else {
				inip = ipMap.get(ip);
				setData(inip, type);
			}
		}
	}

	private void setData(IntranetIP inip, int type) {
		inip.setCount(inip.getCount() + 1);
		inip.setSize(inip.getSize() + size);

		if (type == 0) {
			// IN
			inip.setInSize(inip.getInSize() + size);
		} else if (type == 1) {
			// OUT
			inip.setOutSize(inip.getOutSize() + size);
		}

	}

	public Map<String, IntranetIP> getIpMap() {
		return ipMap;
	}

	@Override
	public void clean() {
		ipMap.clear();
	}

}
