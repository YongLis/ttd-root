package com.ly.ttd.inf.rpc.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个接口为可发布的 Nacos RPC 服务。
 * <p>
 * 会被注册到 Nacos 注册中心，供其他微服务远程调用。
 * <p>
 * 使用方式：
 * <pre>
 *  @RpcService(serviceName = "biz-pay-rcs-dem-srv")
 * public interface FactorAuditService {
 *     void submit(FactorAuditSubmitRequest request);
 * }
 * </pre>
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcService {

    /**
     * 服务名称，对应 Nacos 中的 service name。
     * 若不指定，默认使用接口实现类所在模块的应用名（从 app.properties 读取）。
     */
    String serviceName() default "";

    /**
     * 服务版本，用于灰度路由和版本隔离。
     */
    String version() default "";
}
