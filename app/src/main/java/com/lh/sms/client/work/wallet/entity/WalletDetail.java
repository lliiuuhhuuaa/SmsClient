package com.lh.sms.client.work.wallet.entity;

import com.lh.sms.client.work.msg.entity.Message;

import java.math.BigDecimal;
import java.util.Objects;

import lombok.Data;

/**
 * @author lh
 * @do  钱包明细
 * @date 2019-09-17 19:41
 */
@Data
public class WalletDetail implements Comparable<WalletDetail> {
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 变动之前
     */
    private BigDecimal before;
    /**
     * 变动金额
     */
    private BigDecimal money;
    /**
     * 变动之后
     */
    private BigDecimal after;
    /**
     * 变动类型
     */
    private String type;
    /**
     * 变动时间
     */
    private Long createDate;
    /**
     * 是否为余额
     */
    private Integer balance;
    /**
     * 操作
     */
    private Integer oper;
    /**
     * 备注
     */
    private String remark;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WalletDetail walletDetail = (WalletDetail) o;
        return Objects.equals(id, walletDetail.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(WalletDetail walletDetail) {
        return walletDetail.id.compareTo(this.getId());
    }
}
