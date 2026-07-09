package com.ly.ttd.inf.rpc.core.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RPC 响应协议体。
 * 服务端执行完成后返回给客户端的结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse {

    /** 请求 ID（关联请求） */
    private String requestId;
    /** 是否成功 */
    private boolean success;

    /** 返回值 JSON */
    private Object result;

    /** 返回值类型全限定名 */
    private String returnType;

    /** 异常信息 */
    private String errorMessage;

    /** 异常类型 */
    private String errorType;

    /** 调用耗时（ms） */
    private long costMillis;

    public static RpcResponse ok(Object result, String returnType) {
        return RpcResponse.builder()
                .success(true)
                .result(result)
                .returnType(returnType)
                .build();
    }

    public static RpcResponse fail(String errorMessage, Throwable e) {
        return RpcResponse.builder()
                .success(false)
                .errorMessage(errorMessage)
                .errorType(e.getClass().getName())
                .build();
    }
}
