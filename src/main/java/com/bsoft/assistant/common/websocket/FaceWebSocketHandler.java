package com.bsoft.assistant.common.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.bsoft.assistant.common.projectenum.YesNoEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;

/**
 * WebSocket处理器
 */
public class FaceWebSocketHandler implements WebSocketHandler {

    private static Logger log = LoggerFactory.getLogger(FaceWebSocketHandler.class);
    // 保存所有的用户session
    private static final Map<String, WebSocketSession> clients = new ConcurrentHashMap<>();
    // 会话流水号，long型。自加一增加。
    private static volatile long ccId = 1;

    private final String connectMark = "CONNECT";
    private final String closeMark = "DISCONNECT";

    /**
     *  连接就绪时
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("==================================新连接即将接入：=================================");
        // 保存到session中
//        clients.put(session.getId(), session);
    }

    /**
     * 处理信息
     */
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> webSocketMessage) throws Exception {
//        String sessionId = session.getId();
        String webBody = webSocketMessage.getPayload().toString();

        log.info("\n【来自客户端的消息】:\n"+webSocketMessage.getPayload().toString());
        Map<String, String> msgData = parseMsg(webBody);
        if (msgData.containsKey(connectMark)){
            log.info("新用户登入成功：" + msgData.get("clientId"));
            FaceWebSocketHandler.ccId ++;
            FaceWebSocketHandler.clients.put(msgData.get("clientId"),session);
            // 发送一条信息到前端表示连接成功
            session.sendMessage(new TextMessage(webBody.replace(connectMark,"CONNECTED")));
        }
    }
    private Map<String,String> parseMsg(String webBody){
        Map<String,String> result = new HashMap<>();
        if (StringUtils.isBlank(webBody)) return result;

        String[] msgs = webBody.split("\n");
        String msg0 = msgs[0];
        if (msg0.equalsIgnoreCase("CONNECT")){
            result.put(connectMark, YesNoEnum.YES.getCode().toString());
        }else if (msg0.equalsIgnoreCase("DISCONNECT")){
            result.put(closeMark, YesNoEnum.YES.getCode().toString());
        }
        for (String msg : msgs ) {
            if (msg.contains(":")){
                String[] msgHeaders = msg.split(":");
                result.put(msgHeaders[0], msgHeaders[1]);
            }else{
                result.put(msg, msg);
            }
        }
        return result;
    }

    private String getConnectUserBySocketSession(WebSocketSession session){
        return  FaceWebSocketHandler.clients.keySet().stream()
                .filter(item -> session.equals(FaceWebSocketHandler.clients.get(item))).findFirst().orElse(null);
    }
    /**
     * 处理传输时异常
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
        log.error("处理传输时异常:"+throwable);
    }

    /**
     * 关闭连接时
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        String clientId = getConnectUserBySocketSession(session);
        if (clientId != null){
            FaceWebSocketHandler.clients.remove(clientId);
            FaceWebSocketHandler.ccId -- ;
        }
        log.info("关闭连接,用户已经下线: " + clientId);
        session.close();
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public static int broadcastMsg(String msg){
        String toptic = "MESSAGE\nmessage:";
        TextMessage textMessage = new TextMessage(toptic + msg + "\n\n");
        int res = 0;
        for (String clientId : clients.keySet()) {
            try {
                clients.get(clientId).sendMessage(textMessage);
                res ++ ;
            } catch (IOException e) {
               log.error("消息发送失败",e);
            }
        }
        return  res;
    }
}
