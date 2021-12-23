package com.webmonitor.wyh.frame;


import com.webmonitor.wyh.bean.PacketStatistics;
import com.webmonitor.wyh.chart.ChartCreater;
import com.webmonitor.wyh.statistics.Counter;
import com.webmonitor.wyh.utils.FormatTime;
import com.webmonitor.wyh.utils.RandomAccessQueue;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;


import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.List;

public class MyChartFrame extends JFrame implements Runnable {

	public final static int REALTIME_NUM_BARCHART = 1; // 实时-包数量-柱状图

	public final static int TOTAL_NUM_BARCHART = 2; // 总-包数量-柱状图

	public final static int REALTIME_SIZE_BARCHART = 3; // 实时-流量-柱状图

	public final static int TOTAL_SIZE_BARCHART = 4; // 总-流量-柱状图

	public final static int REALTIME_NUM_LINECHART = 5; // 实时-包数量-折线图 所有包

	public final static int REALTIME_SIZE_LINECHART = 6; // 实时-流量-折线图 所有包

	public final static int TOTAL_NUM_PIECHART = 7; // 总-包数量-饼状图

	public final static int TOTAL_SIZE_PIECHART = 8; // 总-流量-饼状图

	private Counter counter = Counter.newInstance();
	private int chartXNum = 20; // 指定图的横坐标数量
	private RandomAccessQueue<List<PacketStatistics>> dataQueue; // 数据队列
	private RandomAccessQueue<String> timeQueue; // 数据包所对应的时间队列
	private ChartPanel chartPanel;
	private boolean destroy = false; // 是否销毁线程
	private JFreeChart chart;
	private ChartCreater chartCreater;
	private int second; // 动态图表更新时间间隔,单位为秒
	private DefaultCategoryDataset dcDataset = new DefaultCategoryDataset();
	private DefaultPieDataset pieDataset = new DefaultPieDataset();
	private JPanel comboBoxPanel;
	private JComboBox cb_chartType; // 显示那种类型的图
	private String[] s1 = { "柱状图", "折线图", "饼状图" };
	private JLabel lable_chartType;
	private JComboBox cb_dataType; // 显示数据类型 实时/总 、 包数量/包大小
	private String[] s2 = { "实时包数量", "总包数量", "实时包大小", "总包大小" };
	private JLabel lable_dataType;
	private JComboBox cb_ifAllData; // 所有包数据或是单独包数据
	private String[] s3 = { "分包", "所有包" };
	private JLabel lable_ifAllData;

	private int chartType; // 绘制何种图

	// 指定图的横坐标数量
	public MyChartFrame(int chartXNum, int chartType) {
		this.chartXNum = chartXNum;
		this.chartType = chartType;
		dataQueue = new RandomAccessQueue<List<PacketStatistics>>(chartXNum);
		timeQueue = new RandomAccessQueue<String>(chartXNum);
		init();
	}

	public MyChartFrame(int chartType) {
		this.chartType = chartType;
		dataQueue = new RandomAccessQueue<List<PacketStatistics>>(chartXNum);
		timeQueue = new RandomAccessQueue<String>(chartXNum);
		init();

	}

	public MyChartFrame() {
		this.chartType = REALTIME_NUM_BARCHART;
		dataQueue = new RandomAccessQueue<List<PacketStatistics>>(chartXNum);
		timeQueue = new RandomAccessQueue<String>(chartXNum);
		init();

	}

	private void init() {
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		setTitle("生成图表");
		setVisible(true);
		setSize(600, 600);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// initComponent();
		addListeneter();
		drawChart();
	}

	private void initComponent() {
		comboBoxPanel = new JPanel();
		cb_chartType = new JComboBox(s1);
		lable_chartType = new JLabel("图类型：");
		cb_dataType = new JComboBox(s2);
		lable_chartType = new JLabel("数据类型：");
		cb_ifAllData = new JComboBox(s3);
		lable_chartType = new JLabel("分包：");
		comboBoxPanel.add(cb_chartType);
		comboBoxPanel.add(lable_chartType);
		comboBoxPanel.add(cb_dataType);
		comboBoxPanel.add(lable_chartType);
		comboBoxPanel.add(cb_ifAllData);
		comboBoxPanel.add(lable_chartType);
		comboBoxPanel.setSize(getWidth(), 40);
		add(comboBoxPanel);
	}

	public void drawChart() {
		chartCreater = ChartCreater.newInstance();
		// 填充数据
		for (int i = 0; i < chartXNum; i++) {
			offer();
		}
		loadData();
		chooseChart();
		chartPanel = new ChartPanel(chart);
		add(chartPanel);
		// 开启线程更新图表
		Thread t = new Thread(this);
		second = 5;
		t.start();
	}

	private void chooseChart() {

		switch (chartType) {
		case REALTIME_NUM_BARCHART:
			chart = chartCreater.createBarChart("实时-包数量-柱状图", "数据包类型", "数据包数量（个）", dcDataset);
			break;
		case TOTAL_NUM_BARCHART:
			chart = chartCreater.createBarChart("总-包数量-柱状图", "数据包类型", "数据包数量（个）", dcDataset);
			break;
		case REALTIME_SIZE_BARCHART:
			chart = chartCreater.createBarChart("实时-流量-柱状图", "数据包类型", "流量大小（字节）", dcDataset);
			break;
		case TOTAL_SIZE_BARCHART:
			chart = chartCreater.createBarChart("总-流量-柱状图", "数据包类型", "流量大小（字节）", dcDataset);
			break;
		case REALTIME_NUM_LINECHART:
			chart = chartCreater.createLineChart("实时-包数量-折线图", "", "数据包数量（个）", dcDataset);
			break;
		case REALTIME_SIZE_LINECHART:
			chart = chartCreater.createLineChart("实时-流量-折线图", "", "流量大小（字节）", dcDataset);
			break;
		case TOTAL_NUM_PIECHART:
			chart = chartCreater.createPieChart("总-包数量-饼状图", pieDataset);
			break;
		case TOTAL_SIZE_PIECHART:
			chart = chartCreater.createPieChart("总-流量-饼状图", pieDataset);
			break;
		default:
			chart = chartCreater.createBarChart("实时-包数量-柱状图", "数据包类型", "数据包数量（个）", dcDataset);
			break;
		}
	}

	@Override
	public void run() {
		updateData();
	}

	// 在开启的新线程中更新数据
	public void updateData() {
		while (!destroy) {
			try {
				// 每隔一秒钟更新一次数据
				Thread.sleep(1000 * second);
				// 取出一对数据
				take();

				// 加入一对数据
				offer();

				// TODO 更新图表
				loadData();
				if(chartType<=6) {
					((CategoryPlot) chart.getPlot()).setDataset(dcDataset);
				}else {
					break;
				}
				

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// 填充数据
	private void offer() {
		if (chartType == REALTIME_NUM_BARCHART || chartType == REALTIME_NUM_LINECHART
				|| chartType == REALTIME_SIZE_BARCHART || chartType == REALTIME_SIZE_LINECHART) {
			dataQueue.offer(counter.getRtList());
		} else {
			dataQueue.offer(counter.getPsList());
		}

		timeQueue.offer(FormatTime.formatSimpleTime(new Date()));
	}

	// 取数据
	private void take() {
		dataQueue.take();
		timeQueue.take();
	}

	public int getChartXNum() {
		return chartXNum;
	}

	public void setChartXNum(int chartXNum) {
		this.chartXNum = chartXNum;
	}

	public int getChartType() {
		return chartType;
	}

	public void setChartType(int chartType) {
		this.chartType = chartType;
	}

	public boolean isDestroy() {
		return destroy;
	}

	public void setDestroy(boolean destroy) {
		this.destroy = destroy;
	}

	private void addListeneter() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setDestroy(true);
			}
		});

	}

	// 往dataset加载中数据
	public void loadData() {
		
		if (chartType == REALTIME_NUM_BARCHART|| chartType == TOTAL_NUM_BARCHART) {
			dcDataset.clear();
			// 添加数据
			for (int i = 0; i < dataQueue.size(); i++) {
				for (int j = 0; j < dataQueue.get(i).size(); j++) {
					// ds.setValue(数据包数量, 类型, 时间);
					// 例如ds.setValue(100, "UDP", "10:20:30");
					dcDataset.setValue((dataQueue.get(i)).get(j).getCount(), (dataQueue.get(i)).get(j).getName(),
							timeQueue.get(i));
//					System.out.println((dataQueue.get(i)).get(j).getCount() + "-" + (dataQueue.get(i)).get(j).getName()
//							+ "-" + timeQueue.get(i));
//					System.out.println("--------------------------");
				}

			}
		} else if (chartType == REALTIME_SIZE_BARCHART|| chartType == TOTAL_SIZE_BARCHART) {
			dcDataset.clear();
			// 添加数据
			for (int i = 0; i < dataQueue.size(); i++) {
				for (int j = 0; j < dataQueue.get(i).size(); j++) {
					// ds.setValue(数据包数量, 类型, 时间);
					// 例如ds.setValue(100, "UDP", "10:20:30");
					dcDataset.setValue((dataQueue.get(i)).get(j).getSize(), (dataQueue.get(i)).get(j).getName(),
							timeQueue.get(i));
//					System.out.println((dataQueue.get(i)).get(j).getSize() + "-" + (dataQueue.get(i)).get(j).getName()
//							+ "-" + timeQueue.get(i));
//					System.out.println("--------------------------");
				}

			}
		}else if(chartType == REALTIME_NUM_LINECHART) {
			dcDataset.clear();
			// 添加数据
			for (int i = 0; i < dataQueue.size(); i++) {
				int sumnum = 0;
				for (int j = 0; j < dataQueue.get(i).size(); j++) {
					sumnum = sumnum + dataQueue.get(i).get(j).getCount();
				}
				dcDataset.setValue(sumnum, "包数量",
						timeQueue.get(i));
//				System.out.println(sumnum + "-" + timeQueue.get(i));
//				System.out.println("--------------------------");

			}
			
		}else if(chartType == REALTIME_SIZE_LINECHART) {
			dcDataset.clear();
			// 添加数据
			for (int i = 0; i < dataQueue.size(); i++) {
				int sumsize = 0;
				for (int j = 0; j < dataQueue.get(i).size(); j++) {
					sumsize = sumsize + dataQueue.get(i).get(j).getSize();
				}
				dcDataset.setValue(sumsize, "包数量",
						timeQueue.get(i));
//				System.out.println(sumsize + "-" + timeQueue.get(i));
//				System.out.println("--------------------------");

			}
		} else if (chartType == TOTAL_NUM_PIECHART) {
			pieDataset.clear();
			// 添加数据
			for (int i = 0; i < dataQueue.size(); i++) {
				for (int j = 0; j < dataQueue.get(i).size(); j++) {
					pieDataset.setValue(dataQueue.get(i).get(j).getName(), dataQueue.get(i).get(j).getCount());
//					System.out.println(dataQueue.get(i).get(j).getName() + "-" + dataQueue.get(i).get(j).getCount()
//							+ "-" + timeQueue.get(i));
//					System.out.println("--------------------------");
				}

			}
		} else if(chartType == TOTAL_SIZE_PIECHART) {
			pieDataset.clear();
			// 添加数据
			for (int i = 0; i < dataQueue.size(); i++) {
				for (int j = 0; j < dataQueue.get(i).size(); j++) {
					pieDataset.setValue(dataQueue.get(i).get(j).getName(), dataQueue.get(i).get(j).getSize());
//					System.out.println(dataQueue.get(i).get(j).getName() + "-" + dataQueue.get(i).get(j).getSize()
//							+ "-" + timeQueue.get(i));
//					System.out.println("--------------------------");
				}

			}
		}

	}

}
