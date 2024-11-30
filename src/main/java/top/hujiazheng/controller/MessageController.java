package top.hujiazheng.controller;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.hujiazheng.Util.JwtUtil;
import top.hujiazheng.Util.Result;
import top.hujiazheng.model.DTO.SendMessageRequest;
import top.hujiazheng.service.impl.MessageServiceImpl;

/**
 * @Author HuXiaoMing
 * @Date 2024/11/29 12:05
 * @Description
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class MessageController {
    @Autowired
    private MessageServiceImpl messageService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 发送消息
     */
    @PostMapping("/chat/send")
    public Result sendMessage(@RequestHeader("Authorization") String token,
                              @RequestBody SendMessageRequest request) {
        try {
            // 从token中获取用户ID
            String actualToken = token.substring(7);
            Claims claims = jwtUtil.getClaimsFromToken(actualToken);
            Long senderId = Long.parseLong(claims.getSubject());

            return messageService.sendMessage(senderId, request.getContent());
        } catch (Exception e) {
            log.error("发送消息失败", e);
            return Result.error("发送失败：" + e.getMessage());
        }
    }

    /**
     * 获取历史消息
     */
    @GetMapping("/chat/history")
    public Result getHistory() {
        try {
            return messageService.getHistory();
        } catch (Exception e) {
            log.error("获取历史消息失败", e);
            return Result.error("获取历史消息失败：" + e.getMessage());
        }
    }

    /**
     * 获取在线用户
     */
    @GetMapping("/users/online")
    public Result getOnlineUsers() {
        try {
            return messageService.getOnlineUsers();
        } catch (Exception e) {
            log.error("获取在线用户失败", e);
            return Result.error("获取在线用户失败：" + e.getMessage());
        }
    }
}
