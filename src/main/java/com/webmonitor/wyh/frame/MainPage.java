package com.webmonitor.wyh.frame;


import com.webmonitor.wyh.bean.FormatPacket;
import com.webmonitor.wyh.deal.CatchPacket;
import com.webmonitor.wyh.deal.TimeUpdatePacket;
import com.webmonitor.wyh.filter.FilterAfterCatch;
import com.webmonitor.wyh.statistics.Counter;
import com.webmonitor.wyh.statistics.DetailProtocolAnalyse;
import com.webmonitor.wyh.statistics.ProtocolAnalyse;
import com.webmonitor.wyh.statistics.StatisticsManager;
import com.webmonitor.wyh.thread.ThreadManager;
import com.webmonitor.wyh.utils.ColorRule;
import com.webmonitor.wyh.utils.FormatTime;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import org.jnetpcap.packet.PcapPacket;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class MainPage extends JFrame {

	public static final int packetListHeight = 300; // 数据包列表长度
	// 数据包列表头
	static final String[] header = { "No.", "Time", "Source", "Destination", "Protocol", "Length", "Info" };

	public static final int CATCHMODE = 0; // 抓包模式
	public static final int OFFLINEMODE = 1; // 加载离线文件数据包模式
	public static final int MONITORMODE = 2; // 长期监控模式

	public static final int NOFILTER = 0; // 不适用显示过滤器
	public static final int USEFILTER = 1; // 使用显示过滤器

	private JTable listTable = null; // 数据包列表
	private DefaultTableModel tableModel = null; // 表格对象的类型
	private JMenuBar mBar = null; // 首部菜单栏
	private DeviceInfoDialog deviceInfoDialog = null;
	private CatchPacket deal = null;
	private BlockingQueue<PcapPacket> packetQueue = null;
	private JTextArea dataTextArea;
	private JTextArea detailTextArea;
	private List<PcapPacket> packets; // 缓存收到的数据包
	private JLabel deviceLabel;
	private JLabel packetCountLabel;
	private boolean suspend = false;// 线程暂停标识
	private String control = ""; // 线程锁绑定的对象
	private int catchMode = 0;
	private int showMode = 0;
	private String filter_expression = "";
	private boolean dofilter = false; // 是否根据表达式进行包过滤
	private List<Integer> colorIndex; // 每一行在过滤颜色数组color[]中的序号
	private Color[] colors;
	private TimeUpdatePacket timeUpdatePacket;
	private int firstPacketFrameId;
	private ProtocolAnalyse ptlAna;
	private Counter counter;
	private JScrollPane listPane, detailPane, dataPane;
	private final static float[] tableColumnWidthPercentage = { 5.0f, 15.0f, 15.0f, 15.0f, 5.0f, 5.0f, 40.0f };
	private String title, deviceinfo;
	private int totalCount = 0, showCount = 0;
	private DetailProtocolAnalyse dpAna = DetailProtocolAnalyse.newInstance();

	public MainPage(String title) {
		super(title);
		this.title = title;
		//设置风格
		loadStyle();

		initComponents();
		layoutComponents();
		openDeviceDataDialog();
		addlistener();
		// loadColorRule();
	}

	// 使用BeautyEye进行风格设置
	private void loadStyle() {
		// FrameBorderStyle.osLookAndFeelDecorated
		// FrameBorderStyle.translucencyAppleLike
		// FrameBorderStyle.translucencySmallShadow
		// FrameBorderStyle.generalNoTranslucencyShadow
		BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.osLookAndFeelDecorated;
		try {
			BeautyEyeLNFHelper.launchBeautyEyeLNF();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void layoutComponents() {

		// setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		// 首部菜单栏
		setJMenuBar(mBar);

		// 数据包列表
		listPane = new JScrollPane(listTable);
		listPane.setSize(getWidth(), 200);
		// add(listPane);

		// 解析出来的详细信息
		// detailTree = new PacketDetailTree();
		// JScrollPane detailPane = new JScrollPane(detailTree);
		// detailPane.setSize(getWidth(), 250);
		// add(detailPane);
		detailTextArea = new JTextArea();
		detailTextArea.setEditable(false);
		detailTextArea.setFont(new Font("Consolas", Font.PLAIN, 12));
		detailPane = new JScrollPane(detailTextArea);
		detailPane.setBorder(BorderFactory.createRaisedBevelBorder()); // 设置边框
		detailPane.setSize(getWidth(), 250);
		// add(detailPane);

		// 数据包二进制码
		dataTextArea = new JTextArea("   \n  \n  \n");
		dataTextArea.setEditable(false);
		dataTextArea.setFont(new Font("Consolas", Font.PLAIN, 12));
		dataPane = new JScrollPane(dataTextArea);
		dataPane.setSize(getWidth(), 200);
		// add(dataPane);

		// //添加分隔面板
		JSplitPane sp1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listPane, detailPane);
		JSplitPane sp2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, sp1, dataPane);
		// add(sp1);
		add(sp2, BorderLayout.CENTER);

		// 底部状态栏
		deviceLabel = new JLabel("网卡：");
		packetCountLabel = new JLabel("总共：0，已显示：0");
		JPanel statePanel = new JPanel();
		statePanel.setSize(getWidth(), 25);
		statePanel.add(deviceLabel);
		statePanel.add(packetCountLabel);
		add(statePanel, BorderLayout.SOUTH);

		// pack(); // 调整窗口大小自动适应组件大小
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 关闭窗口退出程序
		setVisible(true);
	}

	// 初始化组件
	private void initComponents() {
		initJMenuBar();
		initPacketListTable();
	}

	// 初始化首部菜单栏
	private void initJMenuBar() {
		mBar = new MyJMenuBar(this);
	}

	// 初始化数据包列表
	private void initPacketListTable() {
		tableModel = new DefaultTableModel(new Object[0][0], header);
		listTable = new JTable(tableModel) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}// 表格不允许被编辑
		};

		// 设置滚动面板视口大小（超过该大小的行数据，需要拖动滚动条才能看到）
		listTable.setPreferredScrollableViewportSize(new Dimension(getWidth(), 200));

		// 设置数据包列表点击事件
		listTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					onTableClick();
				} else if (e.getClickCount() == 2) {
					onTableDoubleClick();
				}

			}

			private void onTableClick() {

				PcapPacket packet = getClickedPacket();

				// 显示解析的数据包详细信息
				detailTextArea.setText(packet.toString());
				// 关注信息的第一行（默认显示最后一行）
				detailTextArea.setCaretPosition(0);

				// 显示数据包二进制信息
				dataTextArea.setText(packet.toHexdump());
				// 关注信息的第一行（默认显示最后一行）
				dataTextArea.setCaretPosition(0);

			}

			private void onTableDoubleClick() {
				PcapPacket packet = getClickedPacket();
				String detailInfo = dpAna.analyse(packet);
				if (null != detailInfo && !detailInfo.equals("")) {
					new DetailPacketInfoDialog(detailInfo,dpAna.getProtocolName());
				}
			}

			private PcapPacket getClickedPacket() {
				// 得到选中的行列的索引值
				PcapPacket packet;
				firstPacketFrameId = (int) packets.get(0).getFrameNumber();
				int row = listTable.getSelectedRow();
				if (showMode == NOFILTER) {
					int flag = timeUpdatePacket.findFrameById(row + 1);
					// 从临时文件加载，或是直接从list中加载
					if (flag >= 0) {
						packet = timeUpdatePacket.getPacketByFrameId(row + 1, flag);
					} else {
						packet = packets.get(row - timeUpdatePacket.getTotalFilePacketCount());
					}
				} else {
					int frameNum = (int) listTable.getValueAt(row, 0);
					packet = packets.get(frameNum - firstPacketFrameId);
				}
				return packet;
			}

		});
	}

	// 初始化网卡信息对话框
	private void openDeviceDataDialog() {
		deviceInfoDialog = new DeviceInfoDialog(this, "选择要监控的网卡", false);
		deviceInfoDialog.setLocationRelativeTo(this);
	}

	private void addlistener() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				TimeUpdatePacket.deleteTempFile();
			}
		});

		// 设置表格各列宽度占比 tableColumnWidthPercentage
		listPane.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int tW = listTable.getWidth();
				TableColumnModel jTableColumnModel = listTable.getColumnModel();
				int cantCols = jTableColumnModel.getColumnCount();
				TableColumn column;
				for (int i = 0; i < cantCols; i++) {
					column = jTableColumnModel.getColumn(i);
					int pWidth = Math.round(tableColumnWidthPercentage[i] * tW);
					column.setPreferredWidth(pWidth);
				}
			}
		});
	}

	// 加载着色规则
	private void loadColorRule() {
		colors = new ColorRule().loadColorRule();
	}

	// 定时保存数据包
	private void openUpdatePacketTimer() {
		timeUpdatePacket = TimeUpdatePacket.newInstance(packets, this);
	}

	// 初始化要显示的网卡信息
	private void initDeviceData() {

	}

	// 列表显示数据包简略信息
	public void showPacketsInfo() {
		// packets = deal.getPackets();
		Thread showPacketThread = new Thread(new Runnable() {

			@Override
			public void run() {

				// 进行的操作多是在List后追加元素，或是查找某一元素，故使用ArrayList
				packets = new ArrayList<PcapPacket>();
				colorIndex = new ArrayList<Integer>();
				PcapPacket packet;

				// 进行数据定时保存
				openUpdatePacketTimer();
				// 设置每行按指定颜色绘制
				paintPacketColor();

				// ProtocolAnalyse初始化须在Counter之前，因为Counter中需要ProtocolAnalyse中先注册协议
				ptlAna = ProtocolAnalyse.newInstance();
				counter = Counter.newInstance();

				while (true) {
					try {
						if (suspend || isOfflineModeAndQueueIsNull()) {
							synchronized (control) {
								System.out.println("showPacketThread waiting");
								control.wait();
								System.out.println("showPacketThread notify");
							}
						}
						while (packetQueue.size() > 0) {

							packet = packetQueue.take();

							packets.add(packet);

							// 过滤数据包
							if (isDofilter()) {
								int result = FilterAfterCatch.filter(packet, filter_expression);
								if (result == 0) {
									continue;
								} else if (result == -1) {
									setDofilter(false);
									showMessage("过滤器表达式有错误");
									// 挂起线程
									setSuspend(true);
									continue;
								}
							}

							showPacketInTable(packet);

							// 计数器记录
							counter.counter(packet);
							// 记录颜色
							colorIndex.add(ptlAna.getColorIndex());

						}
						// totalCount = packets.size() + timeUpdatePacket.getTotalFilePacketCount();
						// showCount = listTable.getRowCount();
						// setTitle(title+"["+deviceinfo+"]"+""+"总共："+totalCount+" 已显示："+showCount);
						packetCountLabel.setText("总共：" + (packets.size() + timeUpdatePacket.getTotalFilePacketCount())
								+ "，已显示：" + listTable.getRowCount());
						// counter.print();
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
						System.out.println("showPacketThread中断");
						ThreadManager.newInstance().printallThreadState();
					}
				}

			}

		});
		// 线程加入线程组
		showPacketThread.setName("showPacketThread");
		ThreadManager.newInstance().addThread("showPacketThread", showPacketThread);
		showPacketThread.start();

	}

	// 唤醒和挂起线程
	public void setSuspend(boolean suspend) {
		if (!suspend) {
			synchronized (control) {
				control.notifyAll();
			}
			// 关闭显示过滤器
			// setDofilter(false);
		}
		this.suspend = suspend;
	}

	public DefaultTableModel getTableModel() {
		return tableModel;
	}

	public void setTableModel(DefaultTableModel tableModel) {
		this.tableModel = tableModel;
	}

	public List<PcapPacket> getPackets() {
		return packets;
	}

	public void setPackets(List<PcapPacket> packets) {
		this.packets = packets;
	}

	public BlockingQueue<PcapPacket> getPacketQueue() {
		return packetQueue;
	}

	public void setPacketQueue(BlockingQueue<PcapPacket> packetQueue) {
		this.packetQueue = packetQueue;
	}

	public JLabel getDeviceLabel() {
		return deviceLabel;
	}

	public void setDeviceLabel(JLabel deviceLabel) {
		this.deviceLabel = deviceLabel;
	}

	public JLabel getPacketCountLabel() {
		return packetCountLabel;
	}

	public void setPacketCountLabel(JLabel packetCountLabel) {
		this.packetCountLabel = packetCountLabel;
	}

	public int getCatchMode() {
		return catchMode;
	}

	public void setCatchMode(int mode) {
		if (this.catchMode != mode) {
			// 清空统计信息
			StatisticsManager.newInstance().cleanAll();
			this.catchMode = mode;
		}
	}

	public int getShowMode() {
		return showMode;
	}

	public void setShowMode(int showMode) {
		this.showMode = showMode;
	}

	public String getFilter_expression() {
		return filter_expression;
	}

	public void setFilter_expression(String filter_expression) {
		this.filter_expression = filter_expression;
	}

	public boolean isDofilter() {
		return dofilter;
	}

	public void setDofilter(boolean dofilter) {
		this.dofilter = dofilter;
	}

	public Color[] getColors() {
		return colors;
	}

	public void setColors(Color[] colors) {
		this.colors = colors;
	}

	public List<Integer> getColorIndex() {
		return colorIndex;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDeviceinfo() {
		return deviceinfo;
	}

	public void setDeviceinfo(String deviceinfo) {
		this.deviceinfo = deviceinfo;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getShowCount() {
		return showCount;
	}

	public void setShowCount(int showCount) {
		this.showCount = showCount;
	}

	// 封装开始显示数据包代码
	public void startShowPackets(String deviceInfo, BlockingQueue<PcapPacket> packetQueue) {
		clear();
		setCatchMode(MainPage.CATCHMODE);
		setShowMode(MainPage.NOFILTER);
		getDeviceLabel().setText(deviceInfo);
		setDeviceinfo(deviceInfo);
		setPacketQueue(packetQueue);
		showPacketsInfo();
	}

	// 封装在listTable上显示数据包的代码
	private void showPacketInTable(PcapPacket packet) {
		FormatPacket pkt;

		pkt = new FormatPacket();
		pkt.setInfo(ptlAna.analyse(packet)); // ptlAna.analyse()用于统计和显示，功能强大
		pkt.setNo((int) packet.getFrameNumber());
		pkt.setTime(FormatTime.formatTime(new Date(packet.getCaptureHeader().timestampInMillis())));
		pkt.setSrc(ptlAna.getSrc());
		pkt.setDest(ptlAna.getDest());
		pkt.setProtocol(ptlAna.getProtocolName());
		pkt.setLength(packet.getPacketWirelen());

		tableModel.addRow(new Object[] { pkt.getNo(), pkt.getTime(), pkt.getSrc(), pkt.getDest(), pkt.getProtocol(),
				pkt.getLength(), pkt.getInfo() });
	}

	public void clear() {
		// PcapPacket属于直接内存，需要手动释放 cleanup()
		if (null != packets) {
			// for (PcapPacket packet : packets) {
			// packet.cleanup();
			// }
			packets.clear();
		}
		clearUI();
	}

	// 清空UI
	public void clearUI() {
		// 暂时挂起线程用于清空JTable,否则会清空表格的同时添加表格数据，导致无法看到清空的效果
		setSuspend(true);
		// while (tableModel.getRowCount() > 0) {
		// tableModel.removeRow(0);
		// }
		tableModel.setRowCount(0);
		setSuspend(false);

		dataTextArea.setText("");
		detailTextArea.setText("");
	}

	// OFFLINE模式下判断是否已经加载完数据
	private boolean isOfflineModeAndQueueIsNull() {
		if (catchMode == OFFLINEMODE) {
			if (packetQueue.size() == 0) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (packetQueue.size() == 0) {
				return true;
			}
		}
		return false;
	}

	// 用对话框提示信息
	private void showMessage(String message) {
		JOptionPane.showMessageDialog(this, message);
	}

	// 过滤packets中已有的数据包
	public void filterPacketsList() {
		// 挂起当前线程，防止新进来的数据包造成干扰
		setSuspend(true);
		for (PcapPacket packet : packets) {
			// 过滤数据包
			if (isDofilter()) {
				int result = FilterAfterCatch.filter(packet, filter_expression);
				if (result == 0) {
					continue;
				} else if (result == -1) {
					setDofilter(false);
					showMessage("过滤器表达式有错误");
					// 挂起线程
					setSuspend(true);
					continue;
				}
			}
			showPacketInTable(packet);
		}
		setSuspend(false);
	}

	// 显示所有packets(List<PcapList>)中的数据包
	public void showPacketsNoFilter() {
		setDofilter(false);
		filterPacketsList();
	}

	// 根据着色规则为每个生成数据包设定颜色
	public void paintPacketColor() {
		new MyTableCellRenderer(listTable, this);
	}

}
