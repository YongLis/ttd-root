package com.ly.ttd.inf.rpc.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注入远程 Nacos RPC 服务代理。
 * <p>
 * 框架自动创建远程服务的动态代理，调用时通过 Nacos 发现目标实例、
 * 发起 HTTP RPC 请求。
 * <p>
 * 使用方式：
 * <pre>
 *  @Rpcwired(serviceName = "biz-pay-rcs-velocity-srv")
 * private VelocityConfigAdminService velocityConfigAdminService;
 * </pre>
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Rpcwired {
    /**
     * 目标服务名称。为空时优先从被注入接口的 {@code @RpcService}
     * 注解读取，否则必须显式指定。
     */
    String serviceName() default "";

    /**
     * 目标服务版本，用于灰度调用。
     */
    String version() default "";

    /**
     * RPC 调用超时时间（毫秒），默认 3000ms。
     */
    long timeout() default 3000L;
}
