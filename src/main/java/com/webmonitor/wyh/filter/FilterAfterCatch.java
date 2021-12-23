package com.webmonitor.wyh.filter;

import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.winpcap.WinPcap;

//包显示过滤器
public class FilterAfterCatch {

	// 参数：snaplen指定的是可以捕获的最大的byte数，
	// 如果 snaplen的值 比 我们捕获的包的大小要小的话，
	// 那么只有snaplen大小的数据会被捕获并以packet data的形式提供。
	private static int snaplen = 64 * 1024;
	private static PcapBpfProgram filter = new PcapBpfProgram();
	private static WinPcap winPcap = WinPcap.openDead(1, snaplen);

	/**
	 * @param packet
	 *            待过滤的数据包
	 * @param expression
	 *            符合BPF规则的过滤表达式
	 * @return 0表示未通过过滤 -1表示过滤表达式出错
	 */
	public static int filter(PcapPacket packet, String expression) {

		int res = winPcap.compile(filter, expression, 1, 0);
		if (res != 0) {
			System.out.println("Filter error：" + winPcap.getErr());
			return -1;
		}
		int flag = WinPcap.offlineFilter(filter, packet.getCaptureHeader(), packet);
		return flag;
	}



}
