package com.lh.sms.client.work.sms.enums;

/***
 * SM注册状态状态
 */
public enum SmStateEnum {
    REGISTER(1,"已注册至服务","my_sm_icc_id"),
    NO_REGISTER(0,"未注册至服务",""),
    APPLYING(2,"正在申请转移中","apply_transfer"),
    DELETE(-1,"",""),
    ;
    private Integer value;
    private String name;
    private String table;
    SmStateEnum(Integer value,String name,String table){
        this.value = value;
        this.name = name;
        this.table = table;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getTable() {
        return table;
    }

    /**
     * 获取元素
     *
     * @param value
     * @return
     */
    public static SmStateEnum getEnum(Integer value) {
        SmStateEnum[] values = SmStateEnum.values();
        for (SmStateEnum em : values) {
            if (em.getValue().equals(value)) {
                return em;
            }
        }
        return null;
    }
}
