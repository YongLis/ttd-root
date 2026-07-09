package com.ly.ttd.monitor.jvm;

import com.ly.ttd.monitor.AbstractMonitorService;
import com.ly.ttd.monitor.TypeEnum;
import com.ly.ttd.monitor.dto.MonitorDto;
import org.springframework.stereotype.Service;

import java.lang.management.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yong.li
 * @since 2026/3/24 13:08
 */
@Service
public class JvmMonitorService extends AbstractMonitorService {
    @Override
    protected String getType() {
        return TypeEnum.JVM.getCode();
    }

    @Override
    protected Map<String, Object> buildMonitor() {
        Map<String, Object> map = new HashMap<>();
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
        map.put("jvm.heapUsed", new MonitorDto("jvm.heapUsed","已用堆内存" , (double) (memoryUsage.getUsed()/(1024*1024)), "M"));
        map.put("jvm.heapInit", new MonitorDto("jvm.heapInit","初始堆内存" , (double) (memoryUsage.getInit()/(1024*1024)), "M"));
        map.put("jvm.heapCommitted", new MonitorDto("jvm.heapCommitted","可用堆内存" , (double) (memoryUsage.getCommitted()/(1024*1024)), "M"));
        map.put("jvm.heapMax", new MonitorDto("jvm.heapMax","最大堆内存" , (double) (memoryUsage.getMax()/(1024*1024)), "M"));

        // 堆内存总和
        map.put("jvm.heapTotal", new MonitorDto("jvm.heapTotal","堆内存总和" , (double) (memoryUsage.getCommitted()/(1024*1024)), "M"));

        MemoryUsage nonMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();

        map.put("jvm.noneHeapUsed", new MonitorDto("jvm.noneHeapUsed","已用非堆内存" , (double) (nonMemoryUsage.getUsed()/(1024*1024)), "M"));
        map.put("jvm.noneHeapInit", new MonitorDto("jvm.noneHeapInit","初始非堆内存" , (double) (nonMemoryUsage.getInit()/(1024*1024)), "M"));
        map.put("jvm.noneHeapCommitted", new MonitorDto("jvm.noneHeapCommitted","可用非堆内存" , (double) (nonMemoryUsage.getCommitted()/(1024*1024)), "M"));
        map.put("jvm.noneHeapMax", new MonitorDto("jvm.noneHeapMax","最大非堆内存" , (double) (nonMemoryUsage.getMax()/(1024*1024)), "M"));

        // 非堆内存详情
        map.put("jvm.nonHeapCommittedBytes", new MonitorDto("jvm.nonHeapCommittedBytes","非堆内存提交字节数" , (double) nonMemoryUsage.getCommitted(), "B"));
        map.put("jvm.nonHeapInitBytes", new MonitorDto("jvm.nonHeapInitBytes","非堆内存初始字节数" , (double) nonMemoryUsage.getInit(), "B"));
        map.put("jvm.nonHeapMaxBytes", new MonitorDto("jvm.nonHeapMaxBytes","非堆内存最大字节数" , (double) nonMemoryUsage.getMax(), "B"));

        // 堆内存详情
        List<MemoryPoolMXBean> poolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
        for(MemoryPoolMXBean mxBean: poolMXBeans){
            String name = mxBean.getName();
            MemoryUsage poolUsage = mxBean.getUsage();

            // 老年代
            if (name.contains("Old")) {
                map.put("jvm.oldGenBytes", new MonitorDto("jvm.oldGenBytes","堆内存老年代字节数" , (double) poolUsage.getUsed(), "B"));
            }
            // 年轻代Eden区
            else if (name.contains("Eden")) {
                map.put("jvm.edenBytes", new MonitorDto("jvm.edenBytes","堆内存年轻代Eden区字节数" , (double) poolUsage.getUsed(), "B"));
            }
            // 年轻代Survivor区
            else if (name.contains("Survivor")) {
                map.put("jvm.survivorBytes", new MonitorDto("jvm.survivorBytes","堆内存年轻代Survivor区字节数" , (double) poolUsage.getUsed(), "B"));
            }
            // 元空间
            else if (name.contains("Metaspace")) {
                map.put("jvm.metaspaceBytes", new MonitorDto("jvm.metaspaceBytes","元空间字节数" , (double) poolUsage.getUsed(), "B"));
            }
        }

        // GC信息
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        long fullGcCount = 0;
        long youngGcCount = 0;
        long fullGcTime = 0;
        long youngGcTime = 0;

        for (GarbageCollectorMXBean gcBean : gcBeans) {
            String name = gcBean.getName();
            if (name.contains("Old") || name.contains("Full")) {
                fullGcCount += gcBean.getCollectionCount();
                fullGcTime += gcBean.getCollectionTime();
            } else if (name.contains("Young") || name.contains("Eden")) {
                youngGcCount += gcBean.getCollectionCount();
                youngGcTime += gcBean.getCollectionTime();
            }
        }

        map.put("jvm.fullGcCount", new MonitorDto("jvm.fullGcCount","FullGC次数" , (double) fullGcCount, "次"));
        map.put("jvm.youngGcCount", new MonitorDto("jvm.youngGcCount","YoungGC次数" , (double) youngGcCount, "次"));
        map.put("jvm.fullGcTime", new MonitorDto("jvm.fullGcTime","FullGC耗时" , (double) fullGcTime, "ms"));
        map.put("jvm.youngGcTime", new MonitorDto("jvm.youngGcTime","YoungGC耗时" , (double) youngGcTime, "ms"));

        // 线程信息
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        int totalThreads = threadMXBean.getThreadCount();
        int deadlockedThreads = threadMXBean.findDeadlockedThreads() != null ? threadMXBean.findDeadlockedThreads().length : 0;

        int newThreads = 0;
        int blockedThreads = 0;
        int runnableThreads = 0;
        int terminatedThreads = 0;
        int timedWaitingThreads = 0;
        int waitingThreads = 0;

        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(false, false);
        for (ThreadInfo info : threadInfos) {
            Thread.State state = info.getThreadState();
            switch (state) {
                case NEW:
                    newThreads++;
                    break;
                case BLOCKED:
                    blockedThreads++;
                    break;
                case RUNNABLE:
                    runnableThreads++;
                    break;
                case TERMINATED:
                    terminatedThreads++;
                    break;
                case TIMED_WAITING:
                    timedWaitingThreads++;
                    break;
                case WAITING:
                    waitingThreads++;
                    break;
            }
        }

        map.put("jvm.threadTotal", new MonitorDto("jvm.threadTotal","线程总数量" , (double) totalThreads, "个"));
        map.put("jvm.threadDeadlocked", new MonitorDto("jvm.threadDeadlocked","死锁线程数量" , (double) deadlockedThreads, "个"));
        map.put("jvm.threadNew", new MonitorDto("jvm.threadNew","新建线程数量" , (double) newThreads, "个"));
        map.put("jvm.threadBlocked", new MonitorDto("jvm.threadBlocked","阻塞线程数量" , (double) blockedThreads, "个"));
        map.put("jvm.threadRunnable", new MonitorDto("jvm.threadRunnable","可运行线程数量" , (double) runnableThreads, "个"));
        map.put("jvm.threadTerminated", new MonitorDto("jvm.threadTerminated","终结线程数量" , (double) terminatedThreads, "个"));
        map.put("jvm.threadTimedWaiting", new MonitorDto("jvm.threadTimedWaiting","限时等待线程数量" , (double) timedWaitingThreads, "个"));
        map.put("jvm.threadWaiting", new MonitorDto("jvm.threadWaiting","等待中线程数量" , (double) waitingThreads, "个"));

        return map;
    }
}
