CREATE TABLE `tcc_transaction_log`
(
    `id`           bigint AUTO_INCREMENT PRIMARY KEY,
    `global_tx_id` varchar(64) NOT NULL COMMENT '全局事务ID',
    `branch_id`    varchar(64) NOT NULL COMMENT '分支事务ID',
    `status`       varchar(20) NOT NULL COMMENT 'INIT / TRY / CONFIRMED / CANCELLED',
    `retry_count`  int DEFAULT 0,
    `create_time`  datetime,
    `update_time`  datetime,
    UNIQUE KEY `uk_tx_branch` (`global_tx_id`,`branch_id`)
) ENGINE = InnoDB  AUTO_INCREMENT = 1
    COMMENT ='tcc事务日志表';;
