package com.webmonitor.wyh.deal;


import com.webmonitor.wyh.frame.MainPage;
import org.jnetpcap.packet.PcapPacket;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

//定时检查数据包过多是否，将数据包存入临时文件
public class TimeUpdatePacket {

	private static TimeUpdatePacket timeUpdatePacket;

	private static List<PcapPacket> packets; // 缓存收到的数据包

	private int maxPacketCount = 1000000; // 内存最多存多少数据包

	private PacketDumper packetDumper;

	private int index = 0; // 缓存的数据包序列(如果有多个)，用于数据包命名

	private static String dir = "temp"; // 文件夹名

	private String path_prefix = dir + "/temp"; // 路径前缀，放在temp文件夹下

	private String path_suffix = ".pcap"; // 路径后缀

	private static MainPage parent;

	private ArrayList<Integer> filePacketCountList = new ArrayList<Integer>();

	private int totalFilePacketCount = 0; // 所有临时文件中的数据包总数

	private OfflinePacket offlinePacket;

	// 第一次时调用这个方法获取实例
	public static TimeUpdatePacket newInstance(List<PcapPacket> ps, MainPage pt) {
		if (null == timeUpdatePacket) {
			parent = pt;
			packets = ps;
			timeUpdatePacket = new TimeUpdatePacket();
		}
		return timeUpdatePacket;
	}

	// 之后调用这个方法获取实例
	public static TimeUpdatePacket newInstance() {
		if (null == timeUpdatePacket) {
			timeUpdatePacket = new TimeUpdatePacket();
		}
		return timeUpdatePacket;
	}

	// 每十秒检查一次
	private TimeUpdatePacket() {

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// 当当前缓存的数据包数量大于maxPacketCount，则将数据包存入
				if (packets.size() > maxPacketCount) {
					packetDumper = PacketDumper.newInstance(path_prefix + index + path_suffix); // 例：temp/temp1
					packetDumper.dumperPacket(packets);
					System.out.println(path_prefix + (index++) + path_suffix);

					// 记录文件中数据包数量
					filePacketCountList.add(packets.size());
					totalFilePacketCount = totalFilePacketCount + packets.size();

					// 清空内存中缓存的数据包
					packets.clear();
				}
			}
		}, 0, 1000 * 10);// 每十秒检查一次
	}

	// 若不保存数据包，退出时删除所有临时文件
	public static boolean deleteTempFile() {
		boolean flag = true;
		if (dir != null) {
			File file = new File(dir);
			if (file.exists()) {
				File[] filePaths = file.listFiles();
				for (File f : filePaths) {
					if (f.isFile()) {
						f.delete();
					}
				}
			}
		} else {
			flag = false;
		}
		return flag;
	}

	public int getTotalFilePacketCount() {
		return totalFilePacketCount;
	}

	public void setTotalFilePacketCount(int totalFilePacketCount) {
		this.totalFilePacketCount = totalFilePacketCount;
	}

	/**
	 * 查看所找帧号在那个临时文件中
	 * 
	 * @param id
	 *            所找帧号
	 * @return 所在临时文件序列
	 */
	public int findFrameById(int id) {
		for (int i = 0; i < filePacketCountList.size(); i++) {
			if (filePacketCountList.get(i) > id) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @param id
	 *            所找帧号
	 * @param i
	 *            所在临时文件序列
	 * @return 所找数据包
	 */
	public PcapPacket getPacketByFrameId(int id, int i) {
		int sum=0;	//排在前边临时文件中总包数
		for(int j=0;j<i;j++) {
			sum = sum + filePacketCountList.get(j);
		}
		offlinePacket = OfflinePacket.newInstance();
		BlockingQueue<PcapPacket> packetQueue = new LinkedBlockingQueue<PcapPacket>();
		offlinePacket.setPacketQueue(packetQueue);
		offlinePacket.getChoosedPacketInFile(path_prefix + i + path_suffix,id-sum);
		System.out.println("----"+packetQueue.size());

		try {
			while (!packetQueue.isEmpty()) {
				
				
				PcapPacket packet = packetQueue.take();
				if(packet.getFrameNumber()==id) {
					return packet;
				}
			}
		} catch (InterruptedException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}finally {
			packetQueue.clear();
		}
		return null;
	}
}
