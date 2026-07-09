package com.ly.ttd.inf.rpc.core.autoconfigure;

import com.ly.ttd.inf.rpc.api.constant.RpcConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Nacos RPC 框架配置属性——对应 application.properties 中的 {@code nacos.rpc.*} 前缀。
 */
@Data
@ConfigurationProperties(prefix = "nacos.rpc")
public class NacosRpcProperties {

    // ========== Nacos 注册中心配置 ==========

    /** Nacos 服务端地址，默认本地 8848 */
    private String serverAddr = "127.0.0.1:8848";

    /** Nacos 命名空间，默认公共命名空间 */
    private String namespace = "";

    /** Nacos 分组，默认 DEFAULT_GROUP */
    private String group = RpcConstant.DEFAULT_GROUP;

    /** 服务注册用户名 */
    private String username = "";

    /** 服务注册密码 */
    private String password = "";

    // ========== RPC 服务端配置 ==========

    /** RPC 服务器监听主机，默认 0.0.0.0 */
    private String serverHost = "0.0.0.0";

    /** RPC 服务器监听端口，0 表示随机端口 */
    private int serverPort = RpcConstant.DEFAULT_SERVER_PORT;

    /** RPC 请求路径 */
    private String rpcPath = RpcConstant.RPC_REQUEST_PATH;

    // ========== 应用信息 ==========

    /** 应用名称 */
    private String appName = "unknown";

    /** 应用版本 */
    private String appVersion = "1.0.0";

    /** 当前环境（dev/inte/prod/rc/stable） */
    private String env = "dev";

    // ========== 附加元数据 ==========

    /** 注册到 Nacos 的附加元数据 */
    private Map<String, String> metadata = new HashMap<>();

    // ========== Undertow 配置 ==========

    /** IO 线程数 */
    private int ioThreads = Math.max(Runtime.getRuntime().availableProcessors(), 2);

    /** 工作线程数 */
    private int workerThreads = ioThreads * 8;

    /** 是否使用直接缓冲区 */
    private boolean directBuffers = true;

    // ========== OkHttp 客户端配置 ==========

    /** 连接超时（毫秒） */
    private long connectTimeout = 5000L;

    /** 读取超时（毫秒） */
    private long readTimeout = 10000L;

    /** 写入超时（毫秒） */
    private long writeTimeout = 10000L;

    // ========== 功能开关 ==========

    /** 是否启用 Nacos RPC 框架 */
    private boolean enabled = true;
}
