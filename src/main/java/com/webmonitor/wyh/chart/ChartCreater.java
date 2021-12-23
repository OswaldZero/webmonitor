package com.webmonitor.wyh.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.XYDataset;

public class ChartCreater {

	private static ChartCreater chartCreater = new ChartCreater();
	
	private ChartCreater() {}
	
	public static ChartCreater newInstance() {
		return chartCreater;
	}
	
	/**
	 * 绘制折线图
	 * @param title	标题
	 * @param dataset 数据信息
	 * 
	 * @return
	 */
	public JFreeChart createLineChart(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset) {

		ChartUtils.setChartTheme();
		
		JFreeChart chart = ChartFactory.createLineChart(title, categoryAxisLabel, valueAxisLabel, dataset);
        
		//得到绘图区
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);  

        ChartUtils.setLineRender(plot, true);
        plot.setForegroundAlpha(1.0f);
        
        return chart;
	}
	
	/**
	 * 绘制饼状图
	 * @param title	标题
	 * @param dataset 饼状图数据信息
	 * 
	 * @return
	 */
	public JFreeChart createPieChart(String title, PieDataset dataset) {
		


		ChartUtils.setChartTheme();
		
		JFreeChart chart = ChartFactory.createPieChart(title, dataset);
        
		//得到绘图区
		PiePlot plot = (PiePlot) chart.getPlot();
		//ChartUtils.setPieRender(plot);
        plot.setForegroundAlpha(1.0f);
        
        return chart;
	}

	
	/**
	 * 绘制柱状图
	 * @param title	标题
	 * @param dataset 柱状图数据信息
	 * 
	 * @return
	 */
	public JFreeChart createBarChart(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset) {
		


		ChartUtils.setChartTheme();
		
		JFreeChart chart = ChartFactory.createBarChart(title, categoryAxisLabel, valueAxisLabel, dataset);
        
		//得到绘图区
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setForegroundAlpha(1.0f);
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);  
        
        return chart;
	}

	/**
	 * 绘制时序图
	 * @param title	标题
	 * @param dataset 时序图数据信息
	 * 
	 * @return
	 */
	public JFreeChart createTimeSeriesChart(String title,String timeAxisLabel, String valueAxisLabel, XYDataset dataset) {
		


		ChartUtils.setChartTheme();
		
		JFreeChart chart = ChartFactory.createTimeSeriesChart(title, timeAxisLabel, valueAxisLabel, dataset);
		
		//得到绘图区
		XYPlot plot = (XYPlot) chart.getPlot();
        plot.setForegroundAlpha(1.0f);
        
        return chart;
	}
	
	
	//设置字体，防止中文乱码
//	private void chartFontInit() {
//		//创建主题样式  
//		   StandardChartTheme standardChartTheme=new StandardChartTheme("CN");  
//		   //设置标题字体  
//		   standardChartTheme.setExtraLargeFont(new Font("隶书",Font.BOLD,20));  
//		   //设置图例的字体  
//		   standardChartTheme.setRegularFont(new Font("宋书",Font.PLAIN,15));  
//		   //设置轴向的字体  
//		   standardChartTheme.setLargeFont(new Font("宋书",Font.PLAIN,15));  
//		   //应用主题样式  
//		   ChartFactory.setChartTheme(standardChartTheme); 
//	}
	


}
