package com.ly.ttd.monitor.controller;

import com.ly.ttd.monitor.MonitorFactory;
import com.ly.ttd.monitor.TypeEnum;
import com.ly.ttd.monitor.dto.ApplicationMonitorDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yong.li
 * @since 2026/3/24 12:52
 */
@RestController
@RequestMapping("/api/monitor")
public class MonitorController {

    @GetMapping("/jvm")
    public ApplicationMonitorDto getJvm(){
        return MonitorFactory.getInstance(TypeEnum.JVM.getCode()).getMonitor();
    }


    @GetMapping("/threadPool")
    public ApplicationMonitorDto getThreadPool(){
        return MonitorFactory.getInstance(TypeEnum.THREAD_POOL.getCode()).getMonitor();
    }


}
