package com.ly.ttd.seata;

/**
 * @author yong.li
 * @since 2026/6/4 12:08
 */
public abstract class AbstractTransactionService implements TccTransactionService{

    protected AbstractTransactionService next;


    /**
     * 参与角色
     */
    public abstract String getRole();


}
