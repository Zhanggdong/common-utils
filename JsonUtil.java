package com.huasisoft.ybw.common.json;

import java.text.SimpleDateFormat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * json的处理工具类，增加jsonlib的不足
 *
 */
public class JsonUtil {

	// jackson的objectMapper 设计为单例，其他地方使用时，不要重复创建
	public static ObjectMapper objectMapper = new ObjectMapper();

	public static String writeValueAsString(Object value) {
		ObjectMapper objectMapper = new ObjectMapper();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		objectMapper.setDateFormat(fmt);

		String s = "";
		try {
			s = objectMapper.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return s;
	}

}
