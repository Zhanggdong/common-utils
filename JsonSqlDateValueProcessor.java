package net.risesoft.common.json;

import java.sql.Date;
import java.text.SimpleDateFormat;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

public class JsonSqlDateValueProcessor implements JsonValueProcessor {
	private String format = "yyyy-MM-dd";

	public JsonSqlDateValueProcessor() {

	}

	public JsonSqlDateValueProcessor(String format) {
		this.format = format;
	}

	public Object processArrayValue(Object value, JsonConfig jsonConfig) {
		return process(value, jsonConfig);
	}

	public Object processObjectValue(String key, Object value,
			JsonConfig jsonConfig) {
		return process(value, jsonConfig);
	}
	
	private Object process(Object value, JsonConfig jsonConfig) {
		if (null == value)
			return value;
		if (value instanceof Date) {
			String str = new SimpleDateFormat(format).format((Date) value);
			return str;
		}
		return value.toString();
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}
