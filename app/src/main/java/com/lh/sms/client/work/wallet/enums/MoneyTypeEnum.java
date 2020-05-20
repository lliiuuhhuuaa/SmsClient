package com.lh.sms.client.work.wallet.enums;

public enum MoneyTypeEnum {

	BALANCE_PLUS("balance_plus","余额增加"),//
	BALANCE_MINUS("balance_minus","余额减少"),//余额减少
	RECHARGE("recharge","充值"),
	WITHDRAW("withdraw","提现"),
	SEND_SMS("send_sms","发送公共消息");
	private String value;
	private String notice;

	MoneyTypeEnum(String value,String notice) {
		this.value = value;
		this.notice = notice;
	}

	public String getNotice() {
		return notice;
	}

	public String getValue() {
		return value;
	}
	/**
	 * 获取元素
	 * @param value
	 * @return
	 */
	public static MoneyTypeEnum getEnum(String value){
		MoneyTypeEnum[] values = MoneyTypeEnum.values();
		for(MoneyTypeEnum em : values){
			if(em.getValue().equals(value)){
				return em;
			}
		}
		return null;
	}
	/**
	 * 获取元素
	 * @param value
	 * @return
	 */
	public static String getNotice(String value){
		MoneyTypeEnum[] values = MoneyTypeEnum.values();
		for(MoneyTypeEnum em : values){
			if(em.getValue().equals(value)){
				return em.getNotice();
			}
		}
		return value;
	}
}
