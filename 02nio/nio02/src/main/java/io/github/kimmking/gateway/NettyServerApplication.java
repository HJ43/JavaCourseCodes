package io.github.kimmking.gateway;


import io.github.kimmking.gateway.inbound.HttpInboundServer;

import java.util.ArrayList;
import java.util.List;

public class NettyServerApplication {

    public final static String GATEWAY_NAME = "NIOGateway";
    public final static String GATEWAY_VERSION = "1.0.0";

    public static void main(String[] args) {
        String proxyServer1 = System.getProperty("proxyServer","http://localhost:8088/api/hello");
        String proxyServer2 = System.getProperty("proxyServer","http://www.baidu.com");
        ArrayList<String> proxyServerList = new ArrayList<>();
        proxyServerList.add(proxyServer1);
        proxyServerList.add(proxyServer2);
        String proxyPort = System.getProperty("proxyPort","8888");

        //  http://localhost:8888/api/hello  ==> gateway API
        //  http://localhost:8088/api/hello  ==> backend service

        int port = Integer.parseInt(proxyPort);
        System.out.println(GATEWAY_NAME + " " + GATEWAY_VERSION +" starting...");
        HttpInboundServer server = new HttpInboundServer(port, proxyServerList);
        System.out.println(GATEWAY_NAME + " " + GATEWAY_VERSION +" started at http://localhost:" + port + "/api/hello for server:http://localhost:8088/api/hello or http://www.baidu.com");
        try {
            server.run();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
