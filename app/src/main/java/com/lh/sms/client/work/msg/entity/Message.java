package com.lh.sms.client.work.msg.entity;

import java.util.Objects;

import lombok.Data;

/**
 * @author lh
 * @do
 * @date 2019-09-17 19:41
 */
@Data
public class Message implements Comparable<Message>{
    /**
     * ID
     */
    private Long id;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String text;
    /**
     * 按钮
     */
    private String buttons;
    /**
     * 创建日期
     */
    private Long createDate;
    /**
     * 更新时间
     */
    private Long updateDate;
    /**
     * 状态
     */
    private Integer state;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Message o) {
        return o.id.compareTo(this.getId());
    }
}
