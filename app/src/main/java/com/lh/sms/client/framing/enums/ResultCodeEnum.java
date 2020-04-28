package com.lh.sms.client.framing.enums;

public enum ResultCodeEnum {

    OK(200), // 成功
    ERROR(300), // 失败
    NO_AUTH(400), // 未认证
    NO_PERMISSION(500), // 权限不足
    NECESSITY_PASSWORD(503), // 需要密码
    ;
    private Integer value;

    ResultCodeEnum(Integer value) {
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
    public static ResultCodeEnum getEnum(Integer value) {
        ResultCodeEnum[] values = ResultCodeEnum.values();
        for (ResultCodeEnum em : values) {
            if (em.getValue().equals(value)) {
                return em;
            }
        }
        return null;
    }
}
