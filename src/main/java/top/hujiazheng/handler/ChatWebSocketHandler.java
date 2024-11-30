package top.hujiazheng.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import top.hujiazheng.Util.JwtUtil;
import top.hujiazheng.mapper.MessageMapper;
import top.hujiazheng.mapper.UserMapper;
import top.hujiazheng.model.entity.Message;
import top.hujiazheng.model.entity.User;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author HuXiaoMing
 * @Date 2024/11/29 13:14
 * @Description WebSocket消息处理器
 */
@Component
@Slf4j
public class ChatWebSocketHandler implements WebSocketHandler {

    // 存储所有在线用户的WebSocket会话
    private static final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final MessageMapper messageMapper;

    public ChatWebSocketHandler(ObjectMapper objectMapper, JwtUtil jwtUtil, UserMapper userMapper, MessageMapper messageMapper) {
        this.objectMapper = objectMapper;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
        this.messageMapper = messageMapper;
    }

    /**
     * 将消息转换为Map对象
     *
     * @param chatMessage
     * @return
     */
    private static Map<String, Object> getStringObjectMap(Message chatMessage) {
        Map<String, Object> response = new HashMap<>();
        response.put("type", "message");
        Map<String, Object> data = new HashMap<>();
        data.put("id", chatMessage.getId());
        data.put("senderId", chatMessage.getSenderId());
        data.put("content", chatMessage.getContent());
        data.put("createdAt", chatMessage.getCreatedAt().getTime());
        data.put("senderName", chatMessage.getSenderName());
        data.put("senderAvatar", chatMessage.getSenderAvatar());
        response.put("data", data);
        return response;
    }

    /**
     * 当WebSocket连接建立后调用
     *
     * @param session
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            // 从URL参数中获取token
            String token = extractToken(session);
            log.info("收到WebSocket连接请求，token: {}", token);

            if (token == null) {
                log.error("未提供token");
                session.close(CloseStatus.POLICY_VIOLATION);
                return;
            }

            // 验证token并获取用户ID
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                log.error("无效的token");
                session.close(CloseStatus.POLICY_VIOLATION);
                return;
            }

            // 获取用户信息
            User user = userMapper.findById(userId);
            if (user == null) {
                log.error("用户不存在，userId: {}", userId);
                session.close(CloseStatus.POLICY_VIOLATION);
                return;
            }

            // 存储会话并更新用户状态
            userSessions.put(userId, session);
            userMapper.updateStatus(userId, 1);
            userMapper.updateLastActive(userId);

            // 广播用户上线消息
            broadcastUserStatus("online", user);

            log.info("用户 {} 已连接", user.getUsername());
        } catch (Exception e) {
            log.error("WebSocket连接建立失败", e);
            try {
                session.close(CloseStatus.SERVER_ERROR);
            } catch (IOException ex) {
                log.error("关闭WebSocket连接失败", ex);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        try {
            Long userId = getUserIdFromSession(session);
            if (userId != null) {
                userSessions.remove(userId);
                userMapper.updateStatus(userId, 0);
                User user = userMapper.findById(userId);
                broadcastUserStatus("offline", user);
                log.info("用户 {} 已断开连接", user.getUsername());
            }
        } catch (Exception e) {
            log.error("处理WebSocket连接关闭失败", e);
        }
    }

    /**
     * 处理WebSocket消息
     *
     * @param session
     * @param message
     */
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        try {
            String payload = message.getPayload().toString();
            log.debug("收到消息: {}", payload);

            // 解析消息
            Map<String, Object> messageData = objectMapper.readValue(payload, Map.class);
            String content = (String) messageData.get("content");

            // 验证消息内容
            if (content == null || content.trim().isEmpty()) {
                log.error("消息内容为空");
                return;
            }

            // 获取发送者信息
            Long userId = getUserIdFromSession(session);
            User sender = userMapper.findById(userId);
            if (sender == null) {
                log.error("发送者不存在，userId: {}", userId);
                return;
            }

            // 创建消息对象
            Message chatMessage = new Message();
            chatMessage.setSenderId(Math.toIntExact(userId));
            chatMessage.setContent(content.trim());
            chatMessage.setCreatedAt(new Date());
            chatMessage.setSenderName(sender.getUsername());
            chatMessage.setSenderAvatar(sender.getAvatarUrl());

            // 保存消息到数据库
            try {
                messageMapper.insert(chatMessage);
                log.debug("消息已保存到数据库, id: {}", chatMessage.getId());
            } catch (Exception e) {
                log.error("保存消息到数据库失败", e);
                return;
            }

            // 构建响应消息
            Map<String, Object> response = getStringObjectMap(chatMessage);

            String messageJson = objectMapper.writeValueAsString(response);
            broadcastMessage(messageJson);
        } catch (Exception e) {
            log.error("处理消息失败", e);
        }
    }

    /**
     * 处理WebSocket传输错误
     *
     * @param session
     * @param exception
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("WebSocket传输错误", exception);
        try {
            session.close(CloseStatus.SERVER_ERROR);
        } catch (IOException e) {
            log.error("关闭WebSocket连接失败", e);
        }
    }

    /**
     * 判断是否支持部分消息
     *
     * @return
     */
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 广播消息
     *
     * @param message
     */
    private void broadcastMessage(String message) {
        userSessions.values().forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            } catch (IOException e) {
                log.error("发送消息失败", e);
            }
        });
    }

    /**
     * 广播用户状态
     *
     * @param status
     * @param user
     */
    private void broadcastUserStatus(String status, User user) {
        try {
            if (user == null) {
                log.error("用户对象为空");
                return;
            }

            Map<String, Object> userData = new HashMap<>();
            userData.put("userId", user.getId());
            userData.put("username", user.getUsername());
            userData.put("avatarUrl", user.getAvatarUrl());
            userData.put("status", status);

            Map<String, Object> message = new HashMap<>();
            message.put("type", "user_status");
            message.put("data", userData);

            String statusJson = objectMapper.writeValueAsString(message);
            broadcastMessage(statusJson);
        } catch (JsonProcessingException e) {
            log.error("创建用户状态消息失败", e);
        }
    }

    /**
     * 从WebSocket连接中提取token
     *
     * @param session
     * @return
     */
    private String extractToken(WebSocketSession session) {
        try {
            String query = session.getUri().getQuery();
            log.debug("WebSocket URL query: {}", query);

            if (query != null && query.startsWith("token=")) {
                return query.substring(6);
            }
            return null;
        } catch (Exception e) {
            log.error("提取token失败", e);
            return null;
        }
    }

    /**
     * 从token中提取用户ID
     *
     * @param token
     * @return
     */
    private Long getUserIdFromToken(String token) {
        try {
            return Long.parseLong(jwtUtil.getClaimsFromToken(token).getSubject());
        } catch (Exception e) {
            log.error("解析token失败", e);
            return null;
        }
    }

    /**
     * 从WebSocket连接中提取用户ID
     *
     * @param session
     * @return
     */
    private Long getUserIdFromSession(WebSocketSession session) {
        String token = extractToken(session);
        return token != null ? getUserIdFromToken(token) : null;
    }
}
