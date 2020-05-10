package com.lh.sms.client.data.enums;

/***
 * SM注册状态状态
 */
public enum TablesEnum {
    OBJECT_DATA("object_data","create table if not exists object_data (`key` varchar primary key,`value` varchar not null,`sign` varchar not null)"),
    SM_LIST("sm_list","create table if not exists sm_list (`key` varchar primary key,`value` varchar not null,`sign` varchar not null)"),
    MSG_LIST("msg_list","create table if not exists msg_list (`key` varchar primary key,`value` varchar not null,`sign` varchar not null)"),
    ;
    private String table;
    private String sql;
    TablesEnum(String table, String sql){
        this.table = table;
        this.sql = sql;
    }

    public String getTable() {
        return table;
    }

    public String getSql() {
        return sql;
    }

    /**
     * 获取元素
     *
     * @return
     */
    public static TablesEnum getEnum(String table) {
        TablesEnum[] values = TablesEnum.values();
        for (TablesEnum em : values) {
            if (em.getTable().equals(table)) {
                return em;
            }
        }
        return null;
    }
}
