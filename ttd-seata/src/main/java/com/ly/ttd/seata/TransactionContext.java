package com.ly.ttd.seata;

import lombok.Data;

/**
 * 事务上下文
 * @author yong.li
 * @since 2026/6/4 12:03
 */
@Data
public class TransactionContext {

    private String xid;
    private String branchId;
}
