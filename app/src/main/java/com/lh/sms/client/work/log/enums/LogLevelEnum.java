package com.lh.sms.client.work.log.enums;

public enum LogLevelEnum {

	SUCCESS(1),
	INFO(2),
	WARN(3),
	ERROR(4);

	private Integer value;

	LogLevelEnum(Integer value) {
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
	public static LogLevelEnum getEnum(Integer value){
		LogLevelEnum[] values = LogLevelEnum.values();
		for(LogLevelEnum em : values){
			if(em.getValue().equals(value)){
				return em;
			}
		}
		return null;
	}
}
