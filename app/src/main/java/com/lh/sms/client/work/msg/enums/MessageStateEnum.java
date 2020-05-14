package com.lh.sms.client.work.msg.enums;

public enum MessageStateEnum {

	WAIT(0), //等待处理
	OK(1),  //OK
	IGNORE(2),  //忽略
	DELETE(-1); //删除

	private Integer value;

	MessageStateEnum(Integer value) {
		this.value = value;
	}

	public Integer getValue() {
		return value;
	}
	/**
	 * 获取元素
	 * @param value
	 * @return
	 */
	public static MessageStateEnum getEnum(Integer value){
		MessageStateEnum[] values = MessageStateEnum.values();
		for(MessageStateEnum em : values){
			if(em.getValue().equals(value)){
				return em;
			}
		}
		return null;
	}
}
