package com.webmonitor.wyh.bean;

public class IntranetIP {

	private String ip;
	private long count = 0;
	private long size = 0;
	private long inSize = 0;
	private long outSize = 0;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getInSize() {
		return inSize;
	}

	public void setInSize(long inSize) {
		this.inSize = inSize;
	}

	public long getOutSize() {
		return outSize;
	}

	public void setOutSize(long outSize) {
		this.outSize = outSize;
	}

}
