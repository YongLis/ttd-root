package com.ly.ttd.inf.rpc.core.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RPC 请求协议体。
 * 客户端发送给服务端的请求数据，包含目标方法信息与参数。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest {

    /** 目标服务名称（对应 Nacos service name） */
    private String serviceName;
    /** 目标服务版本 */
    private String version;

    /** 全限定类名（接口名） */
    private String className;

    /** 方法名 */
    private String methodName;

    /** 参数类型全限定名数组 */
    private String[] parameterTypes;

    /** 参数值 JSON 数组 */
    private Object[] parameters;
}
