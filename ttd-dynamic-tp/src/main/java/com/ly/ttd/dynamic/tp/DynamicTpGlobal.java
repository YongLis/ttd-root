//package com.ly.ttd.dynamic.tp;
//
//import org.dromara.dynamictp.common.em.NotifyItemEnum;
//import org.dromara.dynamictp.common.em.RejectedTypeEnum;
//import org.dromara.dynamictp.common.entity.DtpExecutorProps;
//import org.dromara.dynamictp.common.entity.NotifyItem;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.TimeUnit;
//
///**
// * 动态线程池全局配置
// *
// * @author yong.li
// * @since 2026/3/10 10:03
// */
//public class DynamicTpGlobal {
//    private static DtpExecutorProps global = new DtpExecutorProps();
//    static {
//        global.setExecutorType("common");
////        global.setPluginNames();
////        global.setThreadPoolName();
////        global.setThreadPoolAliasName();
//        global.setThreadNamePrefix("ttd");
//        global.setCorePoolSize(10);
//        global.setMaximumPoolSize(50);
//        global.setKeepAliveTime(30);
//        global.setUnit(TimeUnit.SECONDS);
//        global.setQueueCapacity(1000);
////        global.setMaxFreeMemory();
//        global.setRejectedHandlerType(RejectedTypeEnum.CALLER_RUNS_POLICY.getName());
////        global.setRejectEnhanced();
//        global.setAllowCoreThreadTimeOut(false);
//
//        List<NotifyItem> notifyItems = new ArrayList<>();
//        // 变更
//        NotifyItem change = new NotifyItem();
//        change.setType(NotifyItemEnum.CHANGE.getValue());
//        change.setSilencePeriod(120);
//        notifyItems.add(change);
//
//        NotifyItem capacity = new NotifyItem();
//        capacity.setType(NotifyItemEnum.CAPACITY.getValue());
//        capacity.setThreshold(80); // 报警阈值，意思是队列使用率达到80%告警
//        capacity.setCount(1); // # 在一个统计周期内，如果触发阈值的数量达到 count，则触发报警；默认值=1
//        capacity.setPeriod(60); // # 报警统计周期（单位：s），默认值=120
//        capacity.setSilencePeriod(120); //# 报警静默时间（单位：s），0表示不静默，默认值=120
//        notifyItems.add(capacity);
////        # 线程池活性
//        NotifyItem liveness = new NotifyItem();
//        liveness.setThreshold(80); //             # 报警阈值，意思是活性达到70%告警；默认值=70
//        liveness.setCount(1); // ;                     # 在一个统计周期内，如果触发阈值的数量达到 count，则触发报警；默认值=1
//        liveness.setPeriod(60); // ;                   # 报警统计周期（单位：s），默认值=120
//        liveness.setSilencePeriod(0); // ;             # 报警静默时间（单位：s），0表示不静默；默认值=120
//        notifyItems.add(liveness);
//
//        NotifyItem reject = new NotifyItem();
//        reject.setType(NotifyItemEnum.REJECT.getValue());
//        reject.setCount(1); //      # 在一个统计周期内，如果触发拒绝策略次数达到 count，则触发报警；默认值=1
//        reject.setPeriod(60); //    # 报警统计周期（单位：s），默认值=120
//        reject.setSilencePeriod(120); // # 报警静默时间（单位：s），0表示不静默；默认值=120
//        notifyItems.add(reject);
//
//
//        NotifyItem run_timeout = new NotifyItem();
//        run_timeout.setType(NotifyItemEnum.RUN_TIMEOUT.getValue());
//        run_timeout.setCount(1); //      # 在一个统计周期内，如果执行超时次数达到 count，则触发报警；默认值=10
//        run_timeout.setPeriod(60); //    # 报警统计周期（单位：s），默认值=120
//        run_timeout.setSilencePeriod(120); // # 报警静默时间（单位：s），0表示不静默；默认值=120
//        notifyItems.add(run_timeout);
//
//        NotifyItem queue_timeout = new NotifyItem();
//        queue_timeout.setType(NotifyItemEnum.QUEUE_TIMEOUT.getValue());
//        queue_timeout.setCount(1); // # 在一个统计周期内，如果排队超时次数达到 count，则触发报警；默认值=10
//        queue_timeout.setPeriod(60); //  # 报警统计周期（单位：s），默认值=120
//        queue_timeout.setSilencePeriod(120); // # 报警静默时间（单位：s），0表示不静默；默认值=120)
//        notifyItems.add(queue_timeout);
//
//        global.setNotifyItems(notifyItems);
////        global.setPlatformIds();
//        global.setNotifyEnabled(true);
////        global.setRunTimeout();
////        global.setTryInterrupt();
//        global.setQueueTimeout(100);
//        global.setWaitForTasksToCompleteOnShutdown(true);
//        global.setAwaitTerminationSeconds(3);
//        global.setTaskWrapperNames(Set.of("ttl", "mdc", "swTrace"));
////        global.setAwareNames();
//    }
//
//
//    public static DtpExecutorProps getGlobal() {
//        return global;
//    }
//}
