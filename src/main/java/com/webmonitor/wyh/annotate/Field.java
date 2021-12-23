package com.webmonitor.wyh.annotate;

import org.jnetpcap.packet.format.JFormatter.Priority;

import java.lang.annotation.*;

@Target(value= {ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Field {


	public enum Property {
		

		CHECK,
		

		OFFSET,
		

		LENGTH,
		

		VALUE,
		

		DESCRIPTION,
		

		DISPLAY,
		

		MASK,
		

		UNITS,
	}


	public final static String EMPTY = "";


	public final static String DEFAULT_FORMAT = "%s";


	int offset() default -1;


	int length() default -1;


	String name() default EMPTY;


	String display() default EMPTY;


	String nicname() default EMPTY;


	String format() default DEFAULT_FORMAT;


	String units() default EMPTY;


	String description() default EMPTY;


	String parent() default EMPTY;


	public long mask() default 0xFFFFFFFFFFFFFFFFL;


	Priority priority() default Priority.MEDIUM;

}