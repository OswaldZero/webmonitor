package com.webmonitor.wyh.statistics;


import com.webmonitor.wyh.bean.PacketStatistics;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.JProtocol;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 没事啦 计数器，用于收集所需要的数据绘制图表
 */
public class Counter extends Statistics {

	// 单例模式
	private static Counter counter;

	private ProtocolAnalyse ptlAna = ProtocolAnalyse.newInstance();

	private int protocol_id;

	// 各个协议数据包的总数量
	private int[] protocolCount = new int[ProtocolManager.protocolNames.size()];

	// 各个协议数据包的总大小
	private int[] totalProtocolSize = new int[protocolCount.length];

	// 前一个时段数据包总数量
	private int[] prepCount = new int[protocolCount.length];

	// 前一个时段数据包总大小
	private int[] prepSize = new int[protocolCount.length];

	// 实时新的数据包数量
	private int[] realTimepCount = new int[protocolCount.length];

	// 实时新的数据包大小
	private int[] realTimepSize = new int[protocolCount.length];

	private List<PacketStatistics> rtList; // 实时包信息集
	private List<PacketStatistics> psList; // 总包信息集

	private long totalSize = 0;
	private long preTotalSize = 0;
	private long totalCount = 0;
	private long inSize = 0;
	private long outSize = 0;

	private Counter() {
		super();
	}

	/**
	 * @return 内部单例
	 */
	public static Counter newInstance() {
		if (null == counter) {
			counter = new Counter();
		}
		return counter;
	}

	/**
	 * 核心方法，用于统计数据
	 * 
	 * @param packet
	 */
	public void counter(PcapPacket packet) {
		// protocol_id = PacketParse.parseProtocolId(packet);
		// protocolCount[protocol_id]++;
		// totalProtocolSize[protocol_id] += packet.size();
		int i = 0;
		totalSize += packet.size();
		totalCount++;
		if (ptlAna.getType() == ProtocolAnalyse.IN) {
			inSize += packet.size();
		} else if (ptlAna.getType() == ProtocolAnalyse.OUT) {
			outSize += packet.size();
		}
		for (String protocolName : ProtocolManager.protocolNames) {
			if (protocolName.equals(ptlAna.getProtocolName())) {
				protocolCount[i]++;
				totalProtocolSize[i] += packet.size();
				break;
			}
			i++;
		}
	}

	/**
	 * 清空所有计数器
	 */
	public void clean() {
		totalSize = 0;
		preTotalSize = 0;
		totalCount = 0;
		inSize = 0;
		outSize = 0;
		cleanArray(protocolCount);
		cleanArray(totalProtocolSize);
		cleanArray(prepCount);
		cleanArray(prepSize);
		cleanArray(realTimepCount);
		cleanArray(realTimepSize);
	}

	/**
	 * @param array
	 *            需要清空的int数组
	 */
	private void cleanArray(int[] array) {
		for (int i : array) {
			i = 0;
		}
	}

	public void print() {
		for (int i = 0; i < protocolCount.length; i++) {
			if (protocolCount[i] > 0) {
				System.out.println("[" + JProtocol.valueOf(i).name() + "] count:" + protocolCount[i] + ",size:"
						+ totalProtocolSize[i]);
			}
		}
	}

	public int[] getProtocolCount() {
		return protocolCount;
	}

	public void setProtocolCount(int[] protocolCount) {
		this.protocolCount = protocolCount;
	}

	public int[] getTotalProtocolSize() {
		return totalProtocolSize;
	}

	public void setTotalProtocolSize(int[] totalProtocolSize) {
		this.totalProtocolSize = totalProtocolSize;
	}

	public List<PacketStatistics> getRtList() {
		packetData();
		return rtList;
	}

	public void setRtList(List<PacketStatistics> rtList) {
		this.rtList = rtList;
	}

	public List<PacketStatistics> getPsList() {
		packetData();
		return psList;
	}

	public void setPsList(List<PacketStatistics> psList) {
		this.psList = psList;
	}

	public long getTotalSize() {
		return totalSize;
	}

	public long calculateTotalSize() {
		totalSize = 0;
		for (int i = 0; i < protocolCount.length; i++) {
			totalSize = totalSize + totalProtocolSize[i];
		}
		return totalSize;
	}

	// 某个时间间隔内的流量
	public long getRealTimeSize() {
		long realTimeSize = totalSize - preTotalSize;
		preTotalSize = totalSize;
		return realTimeSize;
	}

	public long getPreTotalSize() {
		return preTotalSize;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public long getInSize() {
		return inSize;
	}

	public long getOutSize() {
		return outSize;
	}

	/**
	 * 将数据打包成对象
	 * 
	 * @return 数据打包的包装类
	 */
	public void packetData() {
		psList = new ArrayList<PacketStatistics>();// 总数据
		rtList = new ArrayList<PacketStatistics>();// 实时数据
		PacketStatistics ps;
		PacketStatistics rt;
		for (int i = 0; i < protocolCount.length; i++) {
			if (protocolCount[i] > 0) {
				ps = new PacketStatistics();
				ps.setId(i);
				ps.setName(ProtocolManager.protocolNames.get(i));
				ps.setCount(protocolCount[i]);
				ps.setSize(totalProtocolSize[i]);
				psList.add(ps);

				rt = new PacketStatistics();
				rt.setId(i);
				rt.setName(ProtocolManager.protocolNames.get(i));
				rt.setCount(protocolCount[i] - prepCount[i]);
				rt.setSize(totalProtocolSize[i] - prepSize[i]);
				rtList.add(rt);
				// 将上个时段数据覆盖，实现更新
				prepCount[i] = protocolCount[i];
				prepSize[i] = totalProtocolSize[i];
			}
		}
	}

}
