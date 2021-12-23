package com.webmonitor.wyh.test;

import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.dial.*;
import org.jfree.chart.plot.dial.DialPointer.Pin;
import org.jfree.chart.plot.dial.DialPointer.Pointer;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.StandardGradientPaintTransformer;

import java.awt.*;

/**
 * 仪表盘制作
 * 
 * @author Administrator
 * 
 */
public class DialDemo3 {
	public static void main(String[] args) {
		// 1,数据集合对象 此处为DefaultValueDataset
		DefaultValueDataset dataset = new DefaultValueDataset();
		// 当前指针指向的位置，即：我们需要显示的数据
		dataset.setValue(10D);
		DefaultValueDataset dataset2 = new DefaultValueDataset();
		dataset2.setValue(20D);
		/**
		 * 获取图表区域对象
		 * 
		 * A. setDataSet(int index, DataSet dataSet);
		 * 为表盘设定使用的数据集，通常一个表盘上可能存在多个指针， 因此需要制定该数据集与哪个指针相互关联。 可以将指针想象成数据集的一种体现方式。
		 */
		DialPlot dialplot = new DialPlot();
		dialplot.setView(0.0D, 0.0D, 1.0D, 1.0D);
		dialplot.setDataset(0, dataset);
		dialplot.setDataset(1, dataset2);
		// System.out.println("dataset count:"+dialplot.getDatasetCount());
		/**
		 * 开始设置显示框架结构 B. setDailFrame(DailFrame dailFrame);
		 * 设置表盘的底层面板图像，通常表盘是整个仪表的最底层。
		 */
		StandardDialFrame dialFrame = new StandardDialFrame();
        dialFrame.setBackgroundPaint(Color.lightGray);
        dialFrame.setForegroundPaint(Color.darkGray);
		dialplot.setDialFrame(dialFrame);
		/**
		 * 结束设置显示框架结构 C. setBackground(Color color);
		 * 设置表盘的颜色，可以采用Java内置的颜色控制方式来调用该方法。
		 */
		GradientPaint gradientpaint = new GradientPaint(new Point(), new Color(
				255, 255, 255), new Point(), new Color(170, 170, 220));
		DialBackground dialbackground = new DialBackground(gradientpaint);
		dialbackground
				.setGradientPaintTransformer(new StandardGradientPaintTransformer(
						GradientPaintTransformType.VERTICAL));
		dialplot.setBackground(dialbackground);
		
		// 设置显示在表盘中央位置的信息
		DialTextAnnotation dialtextannotation = new DialTextAnnotation("km/h");
		dialtextannotation.setFont(new Font("Dialog", 1, 14));
		dialtextannotation.setRadius(0.69999999999999996D);
		dialplot.addLayer(dialtextannotation);
		
		/**
		 * 指针指向的数据,用文本显示出来,并指向一个数据集
		 * DialValueIndicator dialvalueindicator = new DialValueIndicator(0);
		 * dialplot.addLayer(dialvalueindicator);
		 */
        DialValueIndicator dvi = new DialValueIndicator(0);
        dvi.setFont(new Font("Dialog", Font.PLAIN, 10));
        dvi.setOutlinePaint(Color.darkGray);
        dvi.setRadius(0.60);
        dvi.setAngle(-103.0);
        dialplot.addLayer(dvi);
        
        DialValueIndicator dvi2 = new DialValueIndicator(1);
        dvi2.setFont(new Font("Dialog", Font.PLAIN, 10));
        dvi2.setOutlinePaint(Color.red);
        dvi2.setRadius(0.60);
        dvi2.setAngle(-77.0);
        dialplot.addLayer(dvi2);
		// 根据表盘的直径大小（0.75），设置总刻度范围
		/**
		 * F. addScale(int index, DailScale dailScale);
		 * 用于设定表盘上的量程，index指明该量程属于哪一个指针所指向的数据集， DailScale指明该量程的样式，如量程的基本单位等信息。
		 * 
		 * StandardDialScale(double lowerBound, double upperBound, double
		 * startAngle, double extent, double majorTickIncrement, int
		 * minorTickCount) new StandardDialScale(-40D, 60D, -120D, -300D,30D);
		 */
		// 对应pointer
		StandardDialScale dialScale1 = new StandardDialScale();
		dialScale1.setLowerBound(-40D); // 最底表盘刻度
		dialScale1.setUpperBound(60D); // 最高表盘刻度
		dialScale1.setStartAngle(-120D); // 弧度为120,刚好与人的正常视觉对齐
		dialScale1.setExtent(-300D); // 弧度为300,刚好与人的正常视觉对齐
		dialScale1.setTickRadius(0.88D); // 值越大,与刻度盘框架边缘越近
		dialScale1.setTickLabelOffset(0.14999999999999999D); // 值越大,与刻度盘刻度越远0
																// .14999999999999999D
		dialScale1.setTickLabelFont(new Font("Dialog", 0, 14)); // 刻度盘刻度字体
		dialplot.addScale(0, dialScale1);
		// 对应pin
		StandardDialScale dialScale2 = new StandardDialScale();
		dialScale2.setLowerBound(0D);
		dialScale2.setUpperBound(100D);
		dialScale2.setStartAngle(-120D);
		dialScale2.setExtent(-300D);
		dialScale2.setTickRadius(0.52D);
		dialScale2.setTickLabelOffset(0.1D);
		dialScale2.setTickLabelFont(new Font("Dialog", 0, 10));  //设置刻度盘字体类型
		//dialScale2.setTickLabelPaint(new Color(255,0,0));  //设置刻度盘字体颜色
		dialScale2.setMinorTickPaint(new Color(255,0,0));  //设置刻度盘小刻度颜色为red
		dialScale2.setMajorTickPaint(new Color(255,0,0));  //设置刻度盘大刻度颜色为red
		dialplot.addScale(1, dialScale2);
		/**
		 * 设置指针 G. addPointer(DailPointer dailPointer);
		 * 用于设定表盘使用的指针样式，JFreeChart中有很多可供选择指针样式，
		 * 用户可以根据使用需要，采用不同的DailPoint的实现类来调用该方法
		 */
		// 指针一
		Pointer pointer = new Pointer(0); // 内部内
		dialplot.addPointer(pointer); // addLayer(pointer);
		dialplot.mapDatasetToScale(0, 0);   //Scale与Dataset对应
		// 指针二
		Pin pin = new Pin(1);
		pin.setPaint(new GradientPaint(new Point(), new Color(255, 0, 0),
				new Point(), new Color(170, 170, 220)));   //设置指针样式
		pin.setRadius(0.50);   //设置指针半径,也就是指针长短
		dialplot.addPointer(pin);	
		dialplot.mapDatasetToScale(1, 1);   //Scale与Dataset对应
		/**
		 * 实例化DialCap H. setCap(DailCap dailCap); 1,设定指针上面的盖帽的样式。 2,也就是设置指针的起点的圆
		 */
		DialCap dialcap = new DialCap();
		dialcap.setRadius(0.0700000000000001D);// 0.10000000000000001D
		dialplot.setCap(dialcap);
		// 生成chart对象
		JFreeChart jfreechart = new JFreeChart(dialplot);
		// 设置标题
		jfreechart.setTitle("仪表盘");
		ChartFrame frame = new ChartFrame("仪表盘", jfreechart,
				true);
		frame.pack();
		// 屏幕居中
		RefineryUtilities.centerFrameOnScreen(frame);
		frame.setVisible(true);
	}
}
