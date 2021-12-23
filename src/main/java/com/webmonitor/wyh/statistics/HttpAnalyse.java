package com.webmonitor.wyh.statistics;

import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Http.Request;
import org.jnetpcap.protocol.tcpip.Http.Response;

import java.util.HashMap;
import java.util.Map;

public class HttpAnalyse extends Statistics{

	//单例模式
	private static HttpAnalyse httpAna = new HttpAnalyse();
	
	// Response
	private int responseCount = 0;
	private int[] statusCode = new int[4]; // 状态码2xx,3xx,4xx,5xx
	private String[] codeStr = {"2xx:Success","3xx:Redirection","4xx:Client Error","5xx:Server Error"};
	// Request
	private int requestCount = 0;
	private int[] method = new int[8]; // 请求方式 GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE
	private String[] methodStr = { "GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "TRACE"};
	private Map<String,Integer> hostCountMap = new HashMap<String,Integer>();
	private Map<String,Integer> hostSizeMap = new HashMap<String,Integer>();
	
	private HttpAnalyse() {
		super();
	}
	
	public static HttpAnalyse newInstance() {
		return httpAna;
	}
	
	public String analyse(Http http,PcapPacket packet) {
		String tipInfo = null;
		colorIndex = 2;
		if (http.hasField(Request.RequestMethod)) {
			requestCount++;
			analyseMethod(http.fieldValue(Request.RequestMethod));
			put(hostCountMap,http.fieldValue(Request.Host),1);
			put(hostSizeMap,http.fieldValue(Request.Host),packet.size());
			tipInfo = http.fieldValue(Request.RequestMethod)+"  "+http.fieldValue(Request.RequestVersion)+"  "+http.fieldValue(Request.Host)+http.fieldValue(Request.RequestUrl);
		}
		if (http.hasField(Response.ResponseCode)) {
			responseCount++;
			int code = analyseStatusCode(http.fieldValue(Response.ResponseCode));
			String codeInfo = statusCodeMeaning(code);
			tipInfo = http.fieldValue(Response.RequestVersion)+"  "+http.fieldValue(Response.ResponseCode)+"("+codeInfo+")";
		}
		return tipInfo;

	}
	
	//请求方式  GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE
	private String analyseMethod(String requestMethod) {
		requestMethod = requestMethod.toUpperCase();
		for(int i=0;i<method.length;i++) {
			if(requestMethod.equals(methodStr[i])) {
				method[i]++;
			}
		}
		return requestMethod;
	}
	
	//状态码   2xx,3xx,4xx,5xx
	private int analyseStatusCode(String sCode) {
		int code = Integer.parseInt(sCode);
		if(code>=200&&code<300) {
			statusCode[0]++;
		}else if(code>=300&&code<400) {
			statusCode[1]++;
		}else if(code>400&&code<500) {
			statusCode[2]++;
		}else if(code>500) {
			statusCode[3]++;
		}
		return code;
	}
	
	private String statusCodeMeaning(int code) {
		String meaning = "";
		switch (code) {
		case 200:
			meaning = "成功";
			break;
		case 201:
			meaning = "已创建";
			break;
		case 202:
			meaning = "已接受";
			break;
		case 203:
			meaning = "非授权信息";
			break;
		case 204:
			meaning = "无内容";
			break;
		case 205:
			meaning = "重置内容";
			break;
		case 206:
			meaning = "部分内容";
			break;
		case 300:
			meaning = "多种选择";
			break;
		case 301:
			meaning = "永久移动";
			break;
		case 302:
			meaning = "临时移动";
			break;
		case 303:
			meaning = "查看其他位置";
			break;
		case 304:
			meaning = "未修改";
			break;
		case 305:
			meaning = "使用代理";
			break;
		case 307:
			meaning = "临时重定向";
			break;
		case 400:
			meaning = "错误请求";
			break;
		case 401:
			meaning = "身份验证错误";
			break;
		case 403:
			meaning = "禁止";
			break;
		case 404:
			meaning = "未找到";
			break;
		case 405:
			meaning = "方法禁用";
			break;
		case 406:
			meaning = "不接受";
			break;
		case 407:
			meaning = "需要代理授权";
			break;
		case 408:
			meaning = "请求超时";
			break;
		case 409:
			meaning = "冲突";
			break;
		case 410:
			meaning = "已删除";
			break;
		case 411:
			meaning = "需要有效长度";
			break;
		case 412:
			meaning = "未满足前提条件";
			break;
		case 413:
			meaning = "请求实体过大";
			break;
		case 414:
			meaning = "请求的URI过长";
			break;
		case 415:
			meaning = "不支持的媒体类型";
			break;
		case 416:
			meaning = "请求范围不符合要求";
			break;
		case 417:
			meaning = "未满足期望值";
			break;
		case 500:
			meaning = "服务器内部错误";
			break;
		case 501:
			meaning = "尚未实施";
			break;
		case 502:
			meaning = "错误网关";
			break;
		case 503:
			meaning = "服务不可用";
			break;
		case 504:
			meaning = "网关超时";
			break;
		case 505:
			meaning = "HTTP版本不支持";
			break;
		default:
			break;
		}
		return meaning;
	}
	
	// 将加入元素map
	private void put(Map<String, Integer> map, String key, int value) {
		if (map.containsKey(key)) {
			map.put(key, (map.get(key) + value));
		} else {
			map.put(key, value);
		}
	}

	public int getResponseCount() {
		return responseCount;
	}

	public int[] getStatusCode() {
		return statusCode;
	}

	public int getRequestCount() {
		return requestCount;
	}

	public int[] getMethod() {
		return method;
	}

	public String[] getMethodStr() {
		return methodStr;
	}

	public Map<String, Integer> getHostCountMap() {
		return hostCountMap;
	}

	public Map<String, Integer> getHostSizeMap() {
		return hostSizeMap;
	}

	public String[] getCodeStr() {
		return codeStr;
	}

	@Override
	public void clean() {
		hostCountMap.clear();
		hostSizeMap.clear();
		cleanArray(statusCode);
		cleanArray(method);
		responseCount = 0;
		requestCount = 0;
	}
	

	/**
	 * @param array
	 *            需要清空的int数组
	 */
	private void cleanArray(int[] array) {
		for (int i : array) {
			i = 0;
		}
	}


	
	
}
