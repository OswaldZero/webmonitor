package com.webmonitor.wyh.bean;

import java.awt.*;
import java.io.Serializable;

public class FormatFilter implements Serializable{

	private String name;	//过滤器名字
	private String grammar;		//过滤器语法
	private Color color;	//显示颜色
	
	
	public FormatFilter(String name, String grammar) {
		this.name = name;
		this.grammar = grammar;
	}
	
	public FormatFilter(String name, String grammar, Color color) {
		this.name = name;
		this.grammar = grammar;
		this.color = color;
	}

	public FormatFilter() {	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGrammar() {
		return grammar;
	}
	public void setGrammar(String grammar) {
		this.grammar = grammar;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 * 重写toString，不然序列化之后显示的是内存地址
	 */
	@Override
	public String toString() {
		return name + " " + grammar;
	}
	
	
}
