package com.webmonitor.wyh.deal;



import com.webmonitor.wyh.bean.FormatFilter;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class FilterDeal {

	//单例模式
	private static FilterDeal fdeal = new FilterDeal();
	private static List<FormatFilter> filters;
	private static String filepath;
	private static File file;
	private ObjectInputStream oin;
	private ObjectOutputStream oout;
	
	private FilterDeal() {
		filters = new LinkedList<FormatFilter>();
	}
	
	public static FilterDeal newInstance(String path) {
		filters.clear();
		filepath = path;
		file = new File(filepath);
		return fdeal;
	}
	
	
	//加载过滤器规则
	public List<FormatFilter> loadFilters() {
		//如果已经加载了文件中的数据，直接返回
		if(filters.size()>0) {
			return filters;
		}
		//读取文件中的数据
		filters = readFromFile();
		
		return filters;
	}
	
	//添加一条新过滤规则
	public void addFilter(FormatFilter filter) {
		filters.add(filter);
		//将新过滤规则写入文件
		writeToFile(filters);
	}
	
	//删除某个过滤规则
	public void deleteFilter(FormatFilter filter) {
		for(FormatFilter f : filters) {
			if(f.getGrammar().equals(filter.getGrammar())&&f.getName().equals(filter.getName())) {
				filters.remove(f);
			}
			writeToFile(filters);
		} 
	}
	
	//将新过滤规则写入文件
	private List<FormatFilter> readFromFile() {
		try {
			oin = new ObjectInputStream(new FileInputStream(file));
			filters = (List<FormatFilter>) oin.readObject();	
			oin.close();
		} catch (IOException | ClassNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return filters;
	}
	
	//读取文件中的规则
	private void writeToFile(List<FormatFilter> filters) {
		try {
			oout = new ObjectOutputStream(new FileOutputStream(file));
			oout.writeObject(filters);
			oout.close();
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	
	//保存修改的filters
	public void saveFilters(List<FormatFilter> fs) {
		writeToFile(fs);
	}
	
//	public static void main(String[] args) {
//		FilterDeal filterDeal = FilterDeal.newInstance();
//		FormatFilter filter1 = new FormatFilter();
//		filter1.setGrammar("ddasd");
//		filter1.setName("dsad");
//		FormatFilter filter2 = new FormatFilter();
//		filter2.setGrammar("aaaaaddasd");
//		filter2.setName("fdad达到");
//		filterDeal.addFilter(filter1);
//		filterDeal.addFilter(filter2);
//		List<FormatFilter> loadFilter = filterDeal.loadFilters();
//		System.out.println(loadFilter.get(1).getName());
//	}
	
	public List<FormatFilter> getFilters() {
		return filters;
	}

	public void setFilters(List<FormatFilter> filters) {
		this.filters = filters;
	}


}
