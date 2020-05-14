package com.lh.sms.client.work.socket.enums;

public enum UserSocketCodeEnum {

	USER_INFO(1001), //用户消息
	UPDATE_NICKNAME(1002),  //更新昵称
	UPDATE_PASSWORD(1003); //修改密码

	private Integer value;

	UserSocketCodeEnum(Integer value) {
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
	public static UserSocketCodeEnum getEnum(Integer value){
		UserSocketCodeEnum[] values = UserSocketCodeEnum.values();
		for(UserSocketCodeEnum em : values){
			if(em.getValue().equals(value)){
				return em;
			}
		}
		return null;
	}
}
