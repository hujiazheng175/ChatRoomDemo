package top.hujiazheng.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.hujiazheng.Util.Result;
import top.hujiazheng.mapper.MessageMapper;
import top.hujiazheng.mapper.UserMapper;
import top.hujiazheng.model.entity.Message;
import top.hujiazheng.model.entity.User;
import top.hujiazheng.service.MessageService;

import java.util.Date;
import java.util.List;

/**
 * @Author HuXiaoMing
 * @Date 2024/11/28 19:24
 * @Description
 */
@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 发送消息
     *
     * @param senderId
     * @param content
     * @return
     */
    @Override
    public Result sendMessage(Long senderId, String content) {
        try {
            Message message = new Message();
            message.setSenderId(Math.toIntExact(senderId));
            message.setContent(content);
            message.setCreatedAt(new Date());

            messageMapper.insert(message);

            // 获取发送者信息
            User sender = userMapper.findById(senderId);
            message.setSenderName(sender.getUsername());
            message.setSenderAvatar(sender.getAvatarUrl());

            return Result.success(message);
        } catch (Exception e) {
            log.error("发送消息失败", e);
            return Result.error("发送失败");
        }
    }

    /**
     * 获取历史消息
     *
     * @return
     */
    @Override
    public Result getHistory() {
        try {
            List<Message> messages = messageMapper.getRecentMessages();
            return Result.success(messages);
        } catch (Exception e) {
            log.error("获取历史消息失败", e);
            return Result.error("获取历史消息失败");
        }
    }

    /**
     * 获取在线用户
     *
     * @return
     */
    @Override
    public Result getOnlineUsers() {
        try {
            // 获取最近20分钟活跃的用户
            List<User> users = userMapper.findRecentActiveUsers();
            return Result.success(users);
        } catch (Exception e) {
            log.error("获取在线用户失败", e);
            return Result.error("获取在线用户失败");
        }
    }
}
