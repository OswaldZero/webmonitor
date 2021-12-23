package com.webmonitor.wyh.protocol;


import com.webmonitor.wyh.annotate.Field;
import org.jnetpcap.packet.format.FormatUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 没事啦 协议解析参考：https://blog.csdn.net/tianxuhong/article/details/74922454
 *
 */
public class DNS extends Protocol {

	private ArrayList<Area> queryList = new ArrayList<Area>();
	private ArrayList<RRArea> answerList = new ArrayList<RRArea>();
	private ArrayList<RRArea> authorityList = new ArrayList<RRArea>();
	private ArrayList<RRArea> additionalList = new ArrayList<RRArea>();

	private int mainOffset;
	private int addOffset;
	private int answersOffset;
	private int authorityOffset;
	private int additionalOffset;
	
	private Set<String> hostSet = new HashSet<String>();
	
	public enum Flag {

		// 1bit 查询/响应标志，0为查询，1为响应
		QR,

		// 4bit 0表示标准查询，1表示反向查询，2表示服务器状态请求
		opcode,

		// ibit 表示授权回答
		AA,

		// 1bit 表示可截断的
		TC,

		// 1bit 表示期望递归
		RD,

		// 1bit 表示可用递归
		RA,

		// 4bit 表示返回码，0表示没有差错，3表示名字差错，2表示服务器错误（Server Failure）
		rcode
	}

	public enum QueriesType {
		// 由域名获得IPv4地址
		A(1, "由域名获得IPv4地址"),

		// 查询域名服务器
		NS(2, "查询域名服务器"),

		// 查询规范名称
		CNAME(5, "查询规范名称"),

		// 开始授权
		SOA(6, "开始授权"),

		// 熟知服务
		WKS(11, "熟知服务"),

		// 把IP地址转换成域名
		PTR(12, "把IP地址转换成域名"),

		// 主机信息
		HINFO(13, "主机信息"),

		// 邮件交换
		MX(15, "邮件交换"),

		// 由域名获得IPv6地址
		AAAA(28, "由域名获得IPv6地址"),

		// 传送整个区的请求
		AXFR(252, "传送整个区的请求"),

		// 对所有记录的请求
		ANY(255, "对所有记录的请求");

		public static QueriesType valuesOf(int id) {
			for (QueriesType c : values()) {
				if (c.id == id) {
					return c;
				}
			}

			return null;
		}

		public final int id;
		public final String description;

		private QueriesType(int id, String description) {
			this.id = id;
			this.description = description;
		}
	}

	private class Area {
		int id;
		String sign;
		String name;
		QueriesType type;
		byte[] clazz = new byte[2];

		@Override
		public String toString() {
			return "\n" + sign + " " + id + ":" + name + "," + type.name() + "," + getQueryClassDescription(clazz) + "\n" 
					+ "\t" + "Host:" + name + "\n" 
					+ "\t" + "Type:" + type.name() + "(" + type.description + ")" + "(" + type.id + ")" + "\n"
					+ "\t" + "Class:" + getQueryClassDescription(clazz) + "\n" ;
		}
	}

	private class RRArea {
		Area area;
		int ttl;
		int dataLength;
		String data;
		
		@Override
		public String toString() {
			return area.toString()
					+ "\t" + "Time to live:" + ttl + "\n"
					+ "\t" + "Data Length:" + dataLength + "\n"
					+ "\t" + area.type.name() +":" + data + "\n";
		}
	}

	/**
	 * @return 会话标识
	 */
	@Field(offset = 0, length = 2 * BYTE, display = "Transaction_ID")
	public String transaction_ID() {
		return "0x" + getByteToHex(0, 2);
	}

	/**
	 * @return 标志
	 */
	@Field(offset = 2 * BYTE, length = 2 * BYTE, display = "Flags")
	public String flags() {
		return "0x" + getByteToHex(2, 2);
	}

	/**
	 * @return 问题数
	 */
	@Field(offset = 4 * BYTE, length = 2 * BYTE, display = "Questions")
	public String questions() {
		return getByteToShort(4) + "";
	}

	/**
	 * @return 回答 资源记录数
	 */
	@Field(offset = 6 * BYTE, length = 2 * BYTE, display = "Answer_RRs")
	public String answer_RRs() {
		return getByteToShort(6) + "";
	}

	/**
	 * @return 授权 资源记录数
	 */
	@Field(offset = 8 * BYTE, length = 2 * BYTE, display = "Authority_RRs")
	public String authority_RRs() {
		return getByteToShort(8) + "";
	}

	/**
	 * @return 附加 资源记录数
	 */
	@Field(offset = 10 * BYTE, length = 2 * BYTE, display = "Additional_RRs")
	public String additional_RRs() {
		return getByteToShort(10) + "";
	}

	/**
	 * @return 请求
	 */
	@Field(offset = 12 * BYTE, display = "Queries")
	public String queries() {
		getQueries();
		return (queryList.size()==0) ? "null" : queryList.toString();
	}
	
	@Field(offset = 16 * BYTE, display = "Answers")
	public String answers() {
		getAnswers();
		return (answerList.size()==0) ? "null" : answerList.toString();
	}
	
	@Field(offset = 24 * BYTE, display = "Authoritys")
	public String authoritys() {
		getAuthoritys();
		return (authorityList.size()==0) ? "null" : authorityList.toString();
	}

	@Field(offset = 32 * BYTE, display = "Additionals")
	public String additionals() {
		getAdditionals();
		return (additionalList.size()==0) ? "null" : additionalList.toString();
	}

	// /**
	// * length不固定
	// * @return 查询主机名
	// */
	// @Field(parent = "Queries",offset = 0, display = "hostName")
	// public String hostName() {
	// return null;
	// }
	//
	// /**
	// * 由于hostName长度不固定，此offset注解仅用于标志先后顺序，并非准确
	// * @return 查询type
	// */
	// @Field(parent = "Queries",offset = 4,length = 2 * BYTE, display = "type")
	// public String type() {
	// return null;
	// }
	//
	// /**
	// * 由于hostName长度不固定，此offset注解仅用于标志先后顺序，并非准确
	// * @return 查询class
	// */
	// @Field(parent = "Queries",offset = 6,length = 2 * BYTE, display = "clazz")
	// public String clazz() {
	// return null;
	// }

	// ---------------flags--------------


	/**
	 * @return 查询/响应标志，0为查询，1为响应
	 */
	@Field(parent = "Flags", offset = 0, length = 1, display = "\tQR", description = "查询/响应标志")
	public String QR() {
		return ((flags0()[0] & 0x80) >> 7) + "";
	}

	/**
	 * @return 0表示标准查询，1表示反向查询，2表示服务器状态请求
	 */
	@Field(parent = "Flags", offset = 1, length = 4, display = "\topCode", description = "查询码")
	public String opCode() {
		return ((flags0()[0] & 0x78) >> 3) + "";
	}

	/**
	 * @return 表示授权回答
	 */
	@Field(parent = "Flags", offset = 5, length = 1, display = "\tAA", description = "授权回答")
	public String AA() {
		return ((flags0()[0] & 0x04) >> 2) + "";
	}

	/**
	 * @return 表示可截断的
	 */
	@Field(parent = "Flags", offset = 6, length = 1, display = "\tTC", description = "可截断的")
	public String TC() {
		return ((flags0()[0] & 0x02) >> 1) + "";
	}

	/**
	 * @return 表示期望递归
	 */
	@Field(parent = "Flags", offset = 7, length = 1, display = "\tRD", description = "期望递归")
	public String RD() {
		return ((flags0()[0] & 0x01)) + "";
	}

	/**
	 * @return 表示可用递归
	 */
	@Field(parent = "Flags", offset = 8, length = 1, display = "\tRA",  description = "可用递归")
	public String RA() {
		return ((flags0()[1] & 0x80) >> 7) + "";
	}

	/**
	 * (9,10,11 三位使用零填充)
	 * 
	 * @return 表示返回码
	 */
	@Field(parent = "Flags", offset = 12, length = 4, display = "\trCode", description = "返回码")
	public String rCode() {
		return ((flags0()[1] & 0x08)) + "";
	}
	// ----------------------------------

	public byte[] flags0() {
		return getByteArray(2, 2);
	}

	private void getQueries() {
		int offset = 12;
		int count = Integer.parseInt(questions());
		for (int i = 0; i < count; i++) {
			queryList.add(getArea(i+1,"Query",offset));
		}
		answersOffset = mainOffset;
	}

	private void getAnswers() {
		int offset = answersOffset;
		int count = Integer.parseInt(answer_RRs());
		for (int i = 0; i < count; i++) {
			answerList.add(getRRArea(i+1,"Answer",offset));
		}
		authorityOffset = mainOffset;
	}

	private void getAuthoritys() {
		int offset = authorityOffset;
		int count = Integer.parseInt(authority_RRs());
		for (int i = 0; i < count; i++) {
			authorityList.add(getRRArea(i+1,"Authority",offset));
		}
		additionalOffset = mainOffset;
	}

	private void getAdditionals() {
		int offset = additionalOffset;
		int count = Integer.parseInt(additional_RRs());
		for (int i = 0; i < count; i++) {
			additionalList.add(getRRArea(i+1,"Additional",offset));
		}
	}
	
	private RRArea getRRArea(int i, String sign, int offset) {
		offset = mainOffset;
		RRArea rrArea = new RRArea();
		rrArea.area = getArea(i, sign, offset);
		offset = mainOffset;
		rrArea.ttl = getRRAreaTtl(offset);
		offset = offset + 4;
		rrArea.dataLength = getRRAreaDataLength(offset);
		offset = offset + 2;
		rrArea.data = getRRAreaData(offset,rrArea.dataLength,rrArea.area.type);
		mainOffset = offset + rrArea.dataLength;
		return rrArea;
	}

	private Area getArea(int id, String sign,int offset) {
		
			Area query = new Area();
			query.id = id;
			query.sign = sign;
			//定义为数组是为了通过引用，获得在getHostName()中修改后的addOffset
			int[] addOffset = {0};
			query.name = getHostName(offset,addOffset);
			offset = addOffset[0];
			query.type = getQueryType(offset);
			offset = offset + 2;
			query.clazz = getQueryClass(offset);
			offset = offset + 2;
			mainOffset = offset;
			return query;
	}

	private QueriesType getQueryType(int offset) {
		return QueriesType.valuesOf(getByteToShort(offset));
	}

	private byte[] getQueryClass(int offset) {
		return getByteArray(offset, 2);
	}

	private String getAreaDescription(Area query, int index, int sign) {
		return sign+ " " + index + ":" + query.name + "," + query.type.name() + ","
				+ getQueryClassDescription(query.clazz);
	}

	private String getQueryClassDescription(byte[] clazz) {
		if (getByteToShort(clazz) == 1) {
			return "IN";
		}
		return unknown;
	}

	/**
	 *  域名（2字节或不定长）：长度不固定，且不使用填充字节，一般该字段表示的就是需要查询的域名（如果是反向查询，则为IP，反向查询即由IP地址反查域名）
	 *  当报文中域名重复出现的时候，该字段使用2个字节的偏移指针来表示。
	 *  比如，在资源记录中，域名通常是查询问题部分的域名的重复，因此用2字节的指针来表示，具体格式是最前面的两个高位是 11，用于识别指针。
	 *  其余的14位从DNS报文的开始处计数（从0开始），指出该报文中的相应字节数。
	 *  一个典型的例子，C00C(1100000000001100，12正好是头部的长度，其正好指向Queries区域的查询名字字段)。
	 * 
	 * @param offset
	 * @param addOffset 用于返回确定offset
	 * @return
	 */
	private String getHostName(int offset, int[] addOffset) {
		//判断是否是重复域名
		//方法：最前面的两个高位是 11
		int beginOffset = offset;
		if((getByte(offset) & 0xFF & 0xc0) == 0xc0) {
			addOffset[0] = offset + 2;
			return getHostName(getByteToShort(offset) & 0x3FFF, new int[1]);
		}
		StringBuffer sb = new StringBuffer();
		// 以00结尾
		while (getByte(offset) != 0) {
			//可能部分引用
			if((getByte(offset) & 0xFF & 0xc0) == 0xc0){
				sb.append(getHostName((getByteToShort(offset) & 0x3FFF), new int[1]));
				addOffset[0] = offset + 2;
				return sb.toString(); 
			}
			int len = getByte(offset);
			sb.append(getByteToString(offset + 1, len) + ".");
			offset = offset + 1 + len;
		}
		// 删除最后一个点
		sb.deleteCharAt(sb.length() - 1);
		addOffset[0] = beginOffset + sb.length() + 2;
		return sb.toString();
	}
	

	private int getRRAreaTtl(int offset) {
		return getByteToInt(offset);
	}

	private int getRRAreaDataLength(int offset) {
		return getByteToShort(offset);
	}

	private String getRRAreaData(int offset, int dataLength,QueriesType type) {
		switch (type) {
		case A:
			return FormatUtils.ip(getByteArray(offset, dataLength));
		case CNAME:
			return getHostName(offset, new int[1]);
		default:
			return getHostName(offset, new int[1]);
		}
	}

}
