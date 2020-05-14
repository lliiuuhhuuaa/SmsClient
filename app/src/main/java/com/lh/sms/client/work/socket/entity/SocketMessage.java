package com.lh.sms.client.work.socket.entity;

import lombok.Data;

@Data
public class SocketMessage{
	private Integer code;
	private String msg;
	private Object body;//消息内容
	public SocketMessage(){}
	public SocketMessage(Integer code){
		this.code = code;
	}
	public SocketMessage(Integer code, String msg){
		this.code = code;
		this.msg = msg;
	}
	public SocketMessage(Integer code, Object body){
		this.code = code;
		this.body = body;
	}
}
