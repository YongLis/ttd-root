package com.ly.ttd.monitor.dtp;

import com.ly.ttd.monitor.AbstractMonitorService;
import com.ly.ttd.monitor.TypeEnum;
import com.ly.ttd.monitor.dto.ThreadPoolDto;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author yong.li
 * @since 2026/3/24 13:10
 */
@Service
public class ThreadPoolMonitorService extends AbstractMonitorService {
    @Resource
    private ApplicationContext context;
    @Override
    protected String getType() {
        return TypeEnum.THREAD_POOL.getCode();
    }

    @Override
    protected Map<String, Object> buildMonitor() {
        Map<String, Object> map = new HashMap<>();
        Map<String, ThreadPoolExecutor> executorMap = context.getBeansOfType(ThreadPoolExecutor.class);
        for(String key: executorMap.keySet()){
            ThreadPoolExecutor executor = executorMap.get(key);
            map.put(key, new ThreadPoolDto(key,
                    executor.getCorePoolSize(),
                    executor.getMaximumPoolSize(),
                    executor.getQueue().size()));

        }
        return map;
    }
}
