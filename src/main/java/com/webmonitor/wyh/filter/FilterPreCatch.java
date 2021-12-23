package com.webmonitor.wyh.filter;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;

//包抓取过滤器

/**
 * @author 没事啦
 *
 */
public class FilterPreCatch {

	private static PcapBpfProgram filter = new PcapBpfProgram();
	
	/**
	 * @param pcap	用于抓包的pcap
	 * @param expression	过滤表达式
	 * @return	0表示表达式正确
	 * 			-1表示表达式有误
	 */
	public static int filter(Pcap pcap,String expression) {
		 int res = pcap.compile(filter, expression, 1, 0);
		 pcap.setFilter(filter);
		 if (res != 0) {
			 System.out.println("Filter error：" + pcap.getErr());
			 return -1;
		 }
		 return 0;
	}
}
