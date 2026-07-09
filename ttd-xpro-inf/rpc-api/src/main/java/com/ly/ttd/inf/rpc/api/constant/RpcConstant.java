package com.ly.ttd.inf.rpc.api.constant;

/**
 * Nacos RPC 框架常量定义。
 */
public interface RpcConstant {

    /** RPC 服务端默认监听端口 */
    int DEFAULT_SERVER_PORT = 19090;

    /** RPC HTTP 请求路径前缀 */
    String RPC_REQUEST_PATH = "/nacos-rpc/invoke";

    /** 应用元数据文件路径 */
    String APP_PROPERTIES_PATH = "META-INF/app.properties";

    /** 元数据 Key：应用名 */
    String META_APP_NAME = "app.name";

    /** 元数据 Key：应用版本 */
    String META_APP_VERSION = "app.version";
    /** 元数据 Key：环境标识 */
    String META_ENV = "env";

    /** 元数据 Key：RPC 端口 */
    String META_RPC_PORT = "rpc.port";

    /** 元数据 Key：启动时间戳 */
    String META_START_TIME = "start.time";

    /** Nacos 默认分组 */
    String DEFAULT_GROUP = "DEFAULT_GROUP";
}
