package com.webmonitor.wyh.bean;



import com.webmonitor.wyh.annotate.Field;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

public class PField {

	private int offset;
	
	private Field field;
	
	private Method method;
	
	public PField(int offset, Field field, Method method) {
		this.offset = offset;
		this.field = field;
		this.method = method;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}
	
	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	private static class sortByOffset implements Comparator<PField>{
		@Override
		public int compare(PField o1, PField o2) {
			return o1.offset-o2.offset;
		}
	}
	
	public static void sortFieldByOffset(PField[] fields) {
		Arrays.sort(fields, new sortByOffset());
	}

}
