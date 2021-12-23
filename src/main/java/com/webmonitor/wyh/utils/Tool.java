package com.webmonitor.wyh.utils;

import org.apache.commons.lang.StringUtils;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tool {

	public static String transFloatToHundred(float f) {
		DecimalFormat df = new DecimalFormat("0.00%");
		return df.format(f);
	}

	public static String formatByteSize(long l) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (l < 1024) {
			fileSizeString = df.format((double) l) + "B";
		} else if (l < 1048576) {
			fileSizeString = df.format((double) l / 1024) + "K";
		} else if (l < 1073741824) {
			fileSizeString = df.format((double) l / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) l / 1073741824) + "G";
		}
		return fileSizeString;
	}

	// 判断ip地址是否为公网IP
	public static boolean isPublicIp(byte[] addr) {
		final byte b0 = addr[0];
		final byte b1 = addr[1];
		// 10.x.x.x/8
		final byte SECTION_1 = 0x0A;
		// 172.16.x.x/12
		final byte SECTION_2 = (byte) 0xAC;
		final byte SECTION_3 = (byte) 0x10;
		final byte SECTION_4 = (byte) 0x1F;
		// 192.168.x.x/16
		final byte SECTION_5 = (byte) 0xC0;
		final byte SECTION_6 = (byte) 0xA8;
		switch (b0) {
		case SECTION_1:
			return false;
		case SECTION_2:
			if (b1 >= SECTION_3 && b1 <= SECTION_4) {
				return false;
			}
		case SECTION_5:
			switch (b1) {
			case SECTION_6:
				return false;
			}
		default:
			return true;

		}
	}

	/**
	 * 将 非简写的IPv6 转换成 简写的IPv6
	 * 
	 * @param fullIPv6
	 *            非简写的IPv6
	 * @return 简写的IPv6
	 */
	public static String parseFullIPv6ToAbbreviation(String fullIPv6) {
		String abbreviation = "";

		// 1,校验 ":" 的个数 不等于7 或者长度不等于39 直接返回空串
		int count = fullIPv6.length() - fullIPv6.replaceAll(":", "").length();
		if (fullIPv6.length() != 39 || count != 7) {
			return abbreviation;
		}

		// 2,去掉每一位前面的0
		String[] arr = fullIPv6.split(":");

		for (int i = 0; i < arr.length; i++) {
			arr[i] = arr[i].replaceAll("^0{1,3}", "");
		}

		// 3,找到最长的连续的0
		String[] arr2 = arr.clone();
		for (int i = 0; i < arr2.length; i++) {
			if (!"0".equals(arr2[i])) {
				arr2[i] = "-";
			}
		}

		Pattern pattern = Pattern.compile("0{2,}");
		Matcher matcher = pattern.matcher(StringUtils.join(arr2, ""));
		String maxStr = "";
		int start = -1;
		int end = -1;
		while (matcher.find()) {
			if (maxStr.length() < matcher.group().length()) {
				maxStr = matcher.group();
				start = matcher.start();
				end = matcher.end();
			}
		}

		// 3,合并
		if (maxStr.length() > 0) {
			for (int i = start; i < end; i++) {
				arr[i] = ":";
			}
		}
		abbreviation = StringUtils.join(arr, ":");
		abbreviation = abbreviation.replaceAll(":{2,}", "::");

		return abbreviation;

	}

	/**
	 * 将 简写的IPv6 转换成 非简写的IPv6
	 * 
	 * @param fullIPv6
	 *            简写的IPv6
	 * @return 非简写的IPv6
	 */
	public static String parseAbbreviationToFullIPv6(String abbreviation) {
		String fullIPv6 = "";

		if ("::".equals(abbreviation)) {
			return "0000:0000:0000:0000:0000:0000:0000:0000";
		}

		String[] arr = new String[] { "0000", "0000", "0000", "0000", "0000", "0000", "0000", "0000" };

		if (abbreviation.startsWith("::")) {
			String[] temp = abbreviation.substring(2, abbreviation.length()).split(":");
			for (int i = 0; i < temp.length; i++) {
				String tempStr = "0000" + temp[i];
				arr[i + 8 - temp.length] = tempStr.substring(tempStr.length() - 4);
			}

		} else if (abbreviation.endsWith("::")) {
			String[] temp = abbreviation.substring(0, abbreviation.length() - 2).split(":");
			for (int i = 0; i < temp.length; i++) {
				String tempStr = "0000" + temp[i];
				arr[i] = tempStr.substring(tempStr.length() - 4);
			}

		} else if (abbreviation.contains("::")) {
			String[] tempArr = abbreviation.split("::");

			String[] temp0 = tempArr[0].split(":");
			for (int i = 0; i < temp0.length; i++) {
				String tempStr = "0000" + temp0[i];
				arr[i] = tempStr.substring(tempStr.length() - 4);
			}

			String[] temp1 = tempArr[1].split(":");
			for (int i = 0; i < temp1.length; i++) {
				String tempStr = "0000" + temp1[i];
				arr[i + 8 - temp1.length] = tempStr.substring(tempStr.length() - 4);
			}

		} else {
			String[] tempArr = abbreviation.split(":");

			for (int i = 0; i < tempArr.length; i++) {
				String tempStr = "0000" + tempArr[i];
				arr[i] = tempStr.substring(tempStr.length() - 4);
			}

		}

		fullIPv6 = StringUtils.join(arr, ":");

		return fullIPv6;

	}

}
