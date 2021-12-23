package com.webmonitor.wyh.deal;

import com.webmonitor.wyh.statistics.Counter;
import com.webmonitor.wyh.statistics.ProtocolAnalyse;
import com.webmonitor.wyh.thread.ThreadManager;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;


import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class CatchPacket implements Runnable {
	private String control = ""; // 线程锁绑定的对象

	private static CatchPacket catchPacket = new CatchPacket(); // 单例模式

	public static final int CATCHMODE = 0; // 抓包模式
	public static final int MONITORMODE = 1; // 长期监控模式

	private int mode = CATCHMODE;

	private StringBuilder errbuf = new StringBuilder();

	private List<PcapIf> alldevs = new LinkedList<PcapIf>();

	public List<String> getAlldevs() {
		int i = 0;
		List<String> devicesInfo = new LinkedList<String>();
		for (PcapIf device : alldevs) {
			System.out.printf("#%d: %s [%s]\n", i++, device.getName(), device.getDescription());
			devicesInfo.add(device.getName() + "[" + device.getDescription() + "]");
		}

		// 将获取到的设备信息用List<String>返回
		return devicesInfo;
	}



	private Pcap pcap = null;

	private int deviceIndex = 1; // 正在监听的设备序号

	private BlockingQueue<PcapPacket> packetQueue;

	private int snaplen = 64 * 1024;
	private int flags = Pcap.MODE_PROMISCUOUS;// 混杂模式 接受所有经过网卡的帧
	private int timeout = 2 * 1000;

	private boolean suspend = false; // 线程暂停标识
	private boolean isStopped = false;
	
	private ProtocolAnalyse ptlAna = ProtocolAnalyse.newInstance();
	private Counter counter = Counter.newInstance();

	private CatchPacket() { // 私有构造方法，不能直接new
	}

	public static CatchPacket newInstance() {
		return catchPacket;
	}

	// 获取当前机器的网卡信息
	public List<String> getAllDevice() {
		// 获取当前机器的网卡信息
		int r = Pcap.findAllDevs(alldevs, errbuf);
		if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
			System.err.printf("Can't read list of devices, error is %s", errbuf.toString());
			return null;
		}

		// 输出网卡信息
		System.out.println("Network devices found:");

		int i = 0;
		List<String> devicesInfo = new LinkedList<String>();
		for (PcapIf device : alldevs) {
			System.out.printf("#%d: %s [%s]\n", i++, device.getName(), device.getDescription());
			devicesInfo.add(device.getName() + "[" + device.getDescription() + "]");
		}

		// 将获取到的设备信息用List<String>返回
		return devicesInfo;

	}

	// 获取网卡信息
	public String getDeviceInfo(int index) {
		return alldevs.get(index).getName() + "[" + alldevs.get(index).getDescription() + "]";
	}

	// 选择要监控的网卡
	public PcapIf selectMonitorDevice(int index) {
		return alldevs.get(index);
	}

	// 打开设备
	public void openDevice(int index) {
		PcapIf device = selectMonitorDevice(index);
		pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);
	}

	// 在指定设备上抓包
	public void catchPacketOnDevice(int index) {

		// 创建一个数据包处理器
		PcapPacketHandler<String> jpacketHandler = new PcapPacketHandler<String>() {

			public void nextPacket(PcapPacket packet, String user) {

				// System.out.printf("Received at %s caplen=%-4d len=%-4d %s\n",
				// new Date(packet.getCaptureHeader().timestampInMillis()),
				// packet.getCaptureHeader().caplen(), // Length
				// // actually
				// // captured
				// packet.getCaptureHeader().wirelen(), // Original length
				// user // User supplied object
				// );
				if (mode == CATCHMODE) {
					packetQueue.offer(packet);
					if (suspend) {
						synchronized (control) {
							try {
								System.out.println("catchPacketThread waiting");
								control.wait();
								System.out.println("catchPacketThread notify");
							} catch (InterruptedException e) {
								// TODO 自动生成的 catch 块
								e.printStackTrace();
							}
						}
					}
				}else if(mode == MONITORMODE){
					ptlAna.analyse(packet);
					counter.counter(packet);
					//counter.print();
				}
			}
		};

		// 循环监听
		// 可以修改 10 为 Integer.MAX_VALUE 来长期监听
		pcap.loop(Integer.MAX_VALUE, jpacketHandler, "jNetPcap rocks!");
		// 监听结束后关闭
		pcap.close();

	}

	public void startCatch(int index) {
		deviceIndex = index;
		// 开启新的线程抓包，主线程负责处理抓到的包
		Thread catchPacketThread = new Thread(this);
		// 线程加入线程组
		catchPacketThread.setName("catchPacketThread");
		ThreadManager.newInstance().addThread("catchPacketThread", catchPacketThread);
		catchPacketThread.start();
	}

	@Override
	public void run() {
		try {
			catchPacketOnDevice(deviceIndex);
		} catch (Exception e) {
			System.out.println("catchPacketThread中止");
		}
	}

	// 唤醒和挂起线程
	public void setSuspend(boolean suspend) {
		if (!suspend) {
			synchronized (control) {
				control.notifyAll();
			}
		}
		this.suspend = suspend;
	}

	public void setSnaplen(int snaplen) {
		this.snaplen = snaplen;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getDeviceIndex() {
		return deviceIndex;
	}

	public void setDeviceIndex(int deviceIndex) {
		this.deviceIndex = deviceIndex;
	}

	public BlockingQueue<PcapPacket> getPacketQueue() {
		return packetQueue;
	}

	public void setPacketQueue(BlockingQueue<PcapPacket> packetQueue) {
		this.packetQueue = packetQueue;
	}

	public Pcap getPcap() {
		return pcap;
	}

	public void setPcap(Pcap pcap) {
		this.pcap = pcap;
	}

	public boolean isStopped() {
		return isStopped;
	}

	public void setStopped(boolean isStopped) {
		this.isStopped = isStopped;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

}
