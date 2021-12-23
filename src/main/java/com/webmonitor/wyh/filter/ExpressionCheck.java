package com.webmonitor.wyh.filter;

import com.webmonitor.wyh.utils.Properties;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapIf;


import java.util.ArrayList;
import java.util.List;

public class ExpressionCheck {
	
	private static PcapBpfProgram filter = new PcapBpfProgram();
	
	//检查表达式是否正确
	/**
	 * @param expression
	 * @return	0表示表达式正确
	 * 			-1表示表达式错误
	 * 			-2表示其他错误
	 */
	public static int checkFilterExpression(String expression) {

		// 获取当前机器的网卡信息
		StringBuilder errbuf = new StringBuilder();

		List<PcapIf> alldevs = new ArrayList<PcapIf>();

		//用于测试表达式
		String filepath = Properties.testExpressionpath;
		
		Pcap pcap = Pcap.openOffline(filepath, errbuf);

		if (pcap == null) {
			System.err.printf("Error while opening device for capture: " + errbuf.toString());
			return -2;
		}

		int res = pcap.compile(filter, expression, 0, 0);
		pcap.setFilter(filter);

		if (res != 0) {
			System.out.println("Filter error：" + pcap.getErr());
			return -1;
		}
		return 0;
	}
}
