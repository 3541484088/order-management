package com.sky.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * WebSocket配置类，用于注册WebSocket的Bean
 */
@Configuration
public class WebSocketConfiguration {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        // 设置 WebSocket 会话的最大文本缓冲区大小
        container.setMaxTextMessageBufferSize(8192);
        // 设置 WebSocket 会话的最大二进制缓冲区大小
        container.setMaxBinaryMessageBufferSize(8192);
        // 设置 WebSocket 会话的异步发送超时时间
        container.setAsyncSendTimeout(10000L);
        return container;
    }
}