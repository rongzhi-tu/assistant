package com.bsoft.assistant.common.websocket;


import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * WebSocket握手拦截器
 */
public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    private static Logger log = Logger.getLogger(HandshakeInterceptor.class);
    /**
     * 握手前
     */
    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {
        return super.beforeHandshake( request, response, wsHandler, attributes );
    }

    /**
     * 握手后
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
        super.afterHandshake(request, response, wsHandler, ex);
    }
}

