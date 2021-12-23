package com.webmonitor.wyh.protocol;

import com.webmonitor.wyh.annotate.Field;
import com.webmonitor.wyh.annotate.FieldParser;
import com.webmonitor.wyh.bean.PField;
import org.jnetpcap.packet.format.FormatUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Protocol{

	public static final int BYTE = 8;
	
	public static final String unknown = "unknown";
	
	public FieldParser fieldParser;
	
	public byte[] payload;

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
	
	public byte[] getByteArray(int offset, int length) {
		byte[] b = new byte[length];
		System.arraycopy(payload, offset, b, 0, length);
		return b;
	}
	
	public byte getByte(int offset) {
		return payload[offset];
	}
	
	public String getByteToHex(int offset) {
	    return FormatUtils.toHexString(payload[offset]); 
	}
	
	public String getByteToHex(int offset,int length){
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<length;i++) {
			sb.append(getByteToHex((offset+i)));
		}
		return sb.toString();
	}
	
	public int getByteToShort(int offset){
		return (payload[offset+1] & 0xff) | ((payload[offset+0] << 8) & 0xff00);
	}
	
	public int getByteToShort(byte[] b){
		return (b[1] & 0xff) | ((b[0] << 8) & 0xff00);
	}
	
	public int getByteToInt(int offset) {
		return payload[offset+3] & 0xFF |   
	            (payload[offset+2] & 0xFF) << 8 |   
	            (payload[offset+1] & 0xFF) << 16 |   
	            (payload[offset+0] & 0xFF) << 24; 
	}
	
	public String getByteToString(int offset,int length) {
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<length;i++) {
			sb.append((char)payload[offset+i]);
		}
		return sb.toString();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		PField[] fields = getFields();
		for(int i=0;i<fields.length;i++) {
			Method method = fields[i].getMethod();
			//System.out.println(method.getName());
			Object result = null;
			try {
				result = method.invoke(this);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
			sb.append(fields[i].getField().display()+":"+(String)result
					+(fields[i].getField().description().equals(Field.EMPTY)?"":"("+fields[i].getField().description()+")")+"\n");
		}
		return sb.toString();
	}
	
	public PField[] getFields() {
		fieldParser = new FieldParser();
		return fieldParser.getFields(this.getClass());
	}
}
