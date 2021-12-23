package com.webmonitor.wyh.test;

import java.util.Timer;
import java.util.TimerTask;

public class TestTTT {
	public static void main(String[] args) {
		timerTest();
	}
	
	public static void timerTest(){
		//创建一个定时器
		Timer timer = new Timer();
		//schedule方法是执行时间定时任务的方法
		timer.schedule(new TimerTask() {
			
			//run方法就是具体需要定时执行的任务
			@Override
			public void run() {
				System.out.println("timer测试!!!");
			}
		}, 0,1000);
	}
	
	
	
}
