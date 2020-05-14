package com.lh.sms.client.work.socket.enums;

public enum MsgHandleTypeEnum {

	NOTICE_MESSAGE("notice_message"), // 通知
	NETWORK_SPEED_TEST("network_speed_test"), // 网络测速
	USER("user"), // 用户相关
	G2048("g2048"), // G2048
	SMS("sms"), // sms
	;
	private String value;

	MsgHandleTypeEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	/**
	 * 获取元素
	 * @param value
	 * @return
	 */
	public static MsgHandleTypeEnum getEnum(String value){
		MsgHandleTypeEnum[] values = MsgHandleTypeEnum.values();
		for(MsgHandleTypeEnum em : values){
			if(em.getValue().equals(value)){
				return em;
			}
		}
		return null;
	}
}
