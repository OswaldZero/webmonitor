package com.webmonitor.wyh.thread;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ThreadManager {

	// catchPacketThread，抓包线程
	public static final String catchPacketThread = "catchPacketThread";
	// showPacketThread，主界面界面显示数据包线程
	public static final String showPacketThread = "showPacketThread";

	// 单例模式
	private static ThreadManager tm = new ThreadManager();

	private Map<String, Thread> allThread = new HashMap<String, Thread>();

	private ThreadManager() {
	}

	public static ThreadManager newInstance() {
		return tm;
	}

	// 添加线程
	public void addThread(String name, Thread thread) {
		allThread.put(name, thread);
	}

	// 停止线程
	public void stopThread(String name) {
		if (allThread.containsKey(name)) {
			Thread thread = allThread.get(name);
			thread.interrupt();
			printallThreadState();
		}
	}

	// 强制停止线程,可能会导致数据不正确
	public void strongStopThread(String name) {
		if (allThread.containsKey(name)) {
			Thread thread = allThread.get(name);
			thread.stop();
		}
	}

	public void printallThreadState() {
		Collection<Thread> values = allThread.values();
		for (Thread t : values) {
			System.out.println(t.getName() + ":" + t.getState());
		}
	}

	public void removeThread(String name) {
		if (allThread.containsKey(name)) {
			allThread.remove(name);
		}
	}

	//清除所有的线程
	public void removeAllThread() {
		if(allThread.size()>0) {
			for (String key : allThread.keySet()) {
				allThread.remove(key);
			}
		}
	}

	//allThread是否有线程
	public boolean hasThread() {
		if(allThread.values().size()>0) {
			return true;
		}
		return false;
	}

}
