package com.wfy.spring.boot.blog.vo;
/**
 *  响应 值对象.（返回对象）
 * @author wfy
 *
 */
public class Response {
	private boolean success; //处理是否成功
	private String message;//处理后消息提示
	private Object body;//返回数据
	
	/** 响应处理是否成功 */
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	/** 响应处理的消息 */
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	/** 响应处理的返回内容 */
	public Object getBody() {
		return body;
	}
	public void setBody(Object body) {
		this.body = body;
	}

	public Response(boolean success, String message) {
		this.success = success;
		this.message = message;
	}
	
	public Response(boolean success, String message, Object body) {
		this.success = success;
		this.message = message;
		this.body = body;
	}
}