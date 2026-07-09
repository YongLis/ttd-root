package com.ly.ttd.seata;

/**
 * TCC事务服务
 * @author yong.li
 * @since 2026/6/4 11:56
 */
public interface TccTransactionService {

    // 尝试提交
    boolean tryCommit(TransactionContext txc);

    // 确认提交
    boolean confirmCommit(TransactionContext txc);

    // 取消提交
    boolean cancelCommit(TransactionContext txc);

}
