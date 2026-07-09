package com.ly.ttd.monitor.mid;

import com.ly.ttd.inf.util.Md5Utils;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;

/**
 * 服务唯一ID生成器
 * @author yong.li
 * @since 2026/3/24 13:44
 */
@Component
public class ServerUniqueIdGenerator {


    public static String generator(String serverName) throws Exception {
        InetAddress inetAddress = InetAddress.getLocalHost();
//        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);
        String hostName = inetAddress.getHostName();
        String ip = inetAddress.getHostAddress();
        return Md5Utils.md5(serverName+""+ hostName+""+ip+(System.currentTimeMillis()));

    }


}
