package com.ly.ttd.inf.rpc.starter;

import com.ly.ttd.inf.rpc.core.autoconfigure.NacosRpcAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * Nacos RPC Starter 自动配置入口。
 * <p>
 * 通过 spring.factories / AutoConfiguration.imports 自动加载，
 * 使用方无需任何额外配置即可启用 Nacos RPC 框架。
 * <p>
 * 如需关闭，在 application.properties 中设置：
 * <pre>
 * nacos.rpc.enabled=false
 * </pre>
 *
 */
@AutoConfiguration
@Import(NacosRpcAutoConfiguration.class)
public class NacosRpcStarterAutoConfiguration {
}
