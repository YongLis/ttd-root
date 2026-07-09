package com.ly.ttd.monitor;

import com.ly.ttd.monitor.dto.ApplicationMonitorDto;
import com.ly.ttd.monitor.mid.ServerUniqueIdGenerator;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * @author yong.li
 * @since 2026/3/24 10:50
 */
@Service
public abstract class AbstractMonitorService {
    @Resource
    private ConfigurableEnvironment environment;

    protected abstract String getType();

    protected abstract Map<String, Object> buildMonitor();


    public ApplicationMonitorDto getMonitor() {
        ApplicationMonitorDto dto = new ApplicationMonitorDto();
        String serverName =environment.getProperty("spring.application.name");
        dto.setAppName(serverName);
        try {
            dto.setHost(InetAddress.getLocalHost().getHostAddress());
            dto.setPort(Integer.parseInt(environment.getProperty("server.port")));
            dto.setMid(ServerUniqueIdGenerator.generator(serverName));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        dto.setMonitorType(getType());
        dto.setData(buildMonitor());
        return dto;
    }
}
