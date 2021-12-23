package com.webmonitor.wyh.utils;

import org.jnetpcap.packet.format.FormatUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatTime {

	//包含 年月日  时分秒 的时间
	public static String formatTime(Date date) {
		DateFormat df = new SimpleDateFormat("HH:mm:ss:SSS   yyyy-MM-dd");
		return df.format(date);
	}
	
	//只包含 时分秒 的时间
	public static String formatSimpleTime(Date date) {
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		return df.format(date);
	}

	//包含 年月日  时分 的时间
	public static String formatTimeHm(Date date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return df.format(date);
	}
	
	//包含 年月日  时分秒 微秒 的时间
	public static String formatDetailTime(long timestampInMillis) {
		return FormatUtils.formatTimeInMillis(timestampInMillis);
	}
}
