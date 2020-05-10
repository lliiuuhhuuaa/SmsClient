package com.lh.sms.client.framing.enums;

public enum ExceptionCodeEnum {

    ICC_ID_BIND(311), // 识别码已经被使用
    ;

    private Integer value;

    ExceptionCodeEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    /**
     * 获取元素
     *
     * @param value
     * @return
     */
    public static ExceptionCodeEnum getEnum(Integer value) {
        ExceptionCodeEnum[] values = ExceptionCodeEnum.values();
        for (ExceptionCodeEnum em : values) {
            if (em.getValue().equals(value)) {
                return em;
            }
        }
        return null;
    }
}
