package com.huasisoft.ybw.common.json;

import java.io.Serializable;

/**
 * 统一返回类
 * 封装统一的json格式
 *
 */
public class JsonResult<T> implements Serializable{
	private static final long serialVersionUID = -2848637767405411748L;
	private static final Integer success = 0 ;
	private static final Integer fail = 1 ;
	
	private Integer exchangeStatus = success ; 
	private Integer errorStatus ;
	private String message = "success";
	private T data ;
	
	
	public Integer getExchangeStatus() {
		return exchangeStatus;
	}
	public void setExchangeStatus(Integer exchangeStatus) {
		this.exchangeStatus = exchangeStatus;
	}
	
	public Integer getErrorStatus() {
		return errorStatus;
	}
	public void setErrorStatus(Integer errorStatus) {
		this.errorStatus = errorStatus;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	
	public JsonResult(T data) {
		super();
		this.exchangeStatus = success;
		this.data = data;
	}
	
	public JsonResult(String errorMessage,Integer errorStatus) {
		super();
		this.message = errorMessage;
		this.exchangeStatus = fail;
		this.errorStatus = errorStatus;
	}
	
}
