package com.webmonitor.wyh.protocol;

import org.jnetpcap.packet.JHeader;
import org.jnetpcap.packet.annotate.Field;
import org.jnetpcap.packet.annotate.FieldSetter;
import org.jnetpcap.packet.annotate.Header;
import org.jnetpcap.packet.annotate.ProtocolSuite;
import org.jnetpcap.protocol.JProtocol;
import org.jnetpcap.protocol.tcpip.Http;

@Header(suite = ProtocolSuite.TCP_IP, nicname = "MP4", parent=Http.class)
public class MP4 extends JHeader{

	public final static int ID = JProtocol.HTTP_ID;
	
	/**
	 * @return	长度
	 */
	@Field(offset = 0, length = 4 * BYTE)
	public int len() {
		return getUByte(4);
	}
	
	@FieldSetter
	public void len(int value) {
		setUByte(4,value);
	}
	
	/**
	 * @return	ftyp
	 */
//	@Field(offset = 4 * BYTE, length = 4 * BYTE)
//	public byte[] ftyp() {
//		byte[] array = new byte[4];
//		return getByteArray(4, array);
//	}
//	
//	@Field(offset = 4 * BYTE, length = 4 * BYTE)
//	public void ftyp() {
//		return getStringByte(4);
//	}
	
}
