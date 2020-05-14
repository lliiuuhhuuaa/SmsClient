package com.lh.sms.client.work.template.enums;

/***
 * SM注册状态状态
 */
public enum TemplateAuthStateEnum {
    OK(1),
    WAIT(0),
    ERROR(2),
    ;
    private Integer value;
    TemplateAuthStateEnum(Integer value){
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
    public static TemplateAuthStateEnum getEnum(Integer value) {
        TemplateAuthStateEnum[] values = TemplateAuthStateEnum.values();
        for (TemplateAuthStateEnum em : values) {
            if (em.getValue().equals(value)) {
                return em;
            }
        }
        return null;
    }
}
