package com.webmonitor.wyh.test;

import org.jnetpcap.nio.JMemory;
import org.jnetpcap.protocol.network.Arp;
import org.jnetpcap.util.Units;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Properties;

public class MaxDirectMemorySize {
	public static void main(String[] args) throws ClassNotFoundException, NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		System.out.println("maxMemoryValue:" + sun.misc.VM.maxDirectMemory());

		System.out.println("================================"+maxDirectMemory());

		ByteBuffer buffer = ByteBuffer.allocateDirect(0);
		Class<?> c = Class.forName("java.nio.Bits");
		Field maxMemory = c.getDeclaredField("maxMemory");
		maxMemory.setAccessible(true);
		synchronized (c) {
			Long maxMemoryValue = (Long) maxMemory.get(null);
			System.out.println("maxMemoryValue:" + maxMemoryValue);
		}
		
		JMemory m = new Arp();
		System.out.println(m.maxDirectMemory());
	}

	public static long maxDirectMemory() {
		long directMemory = 0;
		
		if (directMemory != 0) {
			return directMemory;
		}

		Properties p = System.getProperties();
		String s = p.getProperty("org.jnetsoft.nio.MaxDirectMemorySize");
		s = (s == null) ? p.getProperty("nio.MaxDirectMemorySize") : s;
		s = (s == null) ? p.getProperty("org.jnetsoft.nio.mx") : s;
		s = (s == null) ? p.getProperty("nio.mx") : s;

		if (s != null) {
			directMemory = parseSize(s); // process suffixes kb,mb,gb,tb
		}

		if (directMemory == 0) {
			directMemory = 644444;
		}

		return directMemory;
	}

	static long parseSize(String v) {
		v = v.trim().toLowerCase();
		long multiplier = 1;

		if (v.endsWith("tb")) {
			multiplier = Units.TEBIBYTE;
			v = v.substring(0, v.length() - 2);

		} else if (v.endsWith("gb")) {
			multiplier = Units.GIGIBYTE;
			v = v.substring(0, v.length() - 2);

		} else if (v.endsWith("mb")) {
			multiplier = Units.MEBIBYTE;
			v = v.substring(0, v.length() - 2);

		} else if (v.endsWith("kb")) {
			multiplier = Units.KIBIBYTE;
			v = v.substring(0, v.length() - 2);
		}

		final long size = Long.parseLong(v) * multiplier;

		return size;
	}

}