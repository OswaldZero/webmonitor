package com.webmonitor.wyh.annotate;


import com.webmonitor.wyh.bean.PField;
import com.webmonitor.wyh.protocol.DNS;
import com.webmonitor.wyh.protocol.Protocol;
import org.jnetpcap.packet.format.JFormatter.Priority;


import java.lang.reflect.Method;
import java.util.*;

public class FieldParser {

	private Class parseClass = Field.class;

	private Field annotation;

	public boolean parse(Method method) {
		if (method.isAnnotationPresent(parseClass)) {
			annotation = (Field) method.getAnnotation(parseClass);
			return true;
		}
		return false;
	}

	public PField[] getFields(Class<? extends Protocol> clazz) {
		List<PField> fieldList = new ArrayList<PField>();
		List<PField> sonFieldList = new ArrayList<PField>();
		Method[] methods = clazz.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			boolean flag = parse(methods[i]);
			if (flag == true) {
				String f = getParent();
				if (getParent().equals(Field.EMPTY)||getParent().isEmpty()) {
					fieldList.add(new PField(getOffset(), getField(), methods[i]));
				} else {
					sonFieldList.add(new PField(getOffset(), getField(), methods[i]));
				}
			}
		}
		// 排序PField[]
		PField[] rootFields = sortAndToArray(fieldList);
		PField[] sonFields = sortAndToArray(sonFieldList);
		PField[] fields = sortSonFieldToParent(rootFields, sonFields);
		return fields;
	}

	private PField[] sortAndToArray(List<PField> fieldList) {
		PField[] fields = new PField[fieldList.size()];
		fieldList.toArray(fields);
		PField.sortFieldByOffset(fields);
		return fields;
	}
	
	private PField[] sortSonFieldToParent(PField[] rootFields, PField[] sonFields) {
		List<PField> tempList = Arrays.asList(rootFields);
		ArrayList<PField> rootFieldList = new ArrayList<PField>(tempList);
		Set<String> parents = new HashSet<String>();
		for(int i=0;i<sonFields.length;i++) {
			parents.add(sonFields[i].getField().parent());
		}
		for (int i=0;i<rootFieldList.size();i++) {
			String display = rootFieldList.get(i).getField().display();
			if(parents.contains(display)) {
				for(int j=0;j<sonFields.length;j++) {
					if(sonFields[j].getField().parent().equals(display)) {
						rootFieldList.add(i+j+1, sonFields[j]);
					}
				}
			}
		}
		PField[] fields = new PField[rootFieldList.size()];
		rootFieldList.toArray(fields);
		return fields;
	}

	
	public boolean parse(java.lang.reflect.Field field) {
		if (field.isAnnotationPresent(parseClass)) {
			annotation = (Field) field.getAnnotation(parseClass);
			return true;
		}
		return false;
	}

	public int getOffset() {
		return annotation.offset();
	}

	public int getLength() {
		return annotation.length();
	}

	public String getName() {
		return annotation.name();
	}

	public String getDisplay() {
		return (annotation.display().length() == 0) ? getName() : annotation.display();
	}

	public Priority getProperty() {
		return annotation.priority();
	}

	public String getParent() {
		return annotation.parent();
	}

	public String getDescription() {
		return annotation.description();
	}

	public Field getField() {
		return annotation;
	}

	public static void main(String[] args) {
		FieldParser p = new FieldParser();
		Class clazz = DNS.class;
		Method[] methods = clazz.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			boolean flag = p.parse(methods[i]);
			if (flag == true) {
				System.out.println(p.getOffset());
			}
		}


	}
}
