package com.lh.sms.client.work.socket.enums;

public enum SmsSocketCodeEnum {

	SEND(1001), //已发送
	STATE(1002), //状态
	;
	private Integer value;

	SmsSocketCodeEnum(Integer value) {
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
	public static SmsSocketCodeEnum getEnum(Integer value){
		SmsSocketCodeEnum[] values = SmsSocketCodeEnum.values();
		for(SmsSocketCodeEnum em : values){
			if(em.getValue().equals(value)){
				return em;
			}
		}
		return null;
	}
}
