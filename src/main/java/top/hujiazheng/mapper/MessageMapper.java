package top.hujiazheng.mapper;

import org.apache.ibatis.annotations.*;
import top.hujiazheng.model.entity.Message;

import java.util.List;

/**
 * @Author HuXiaoMing
 * @Date 2024/11/28 19:28
 * @Description
 */
@Mapper
public interface MessageMapper {
    @Insert("INSERT INTO messages (sender_id, content, created_at) VALUES (#{senderId}, #{content}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Message message);

    @Select("SELECT m.*, u.username as sender_name, u.avatar_url as sender_avatar " +
            "FROM messages m " +
            "JOIN users u ON m.sender_id = u.id " +
            "ORDER BY m.created_at ASC LIMIT 100")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "senderId", column = "sender_id"),
            @Result(property = "content", column = "content"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "senderName", column = "sender_name"),
            @Result(property = "senderAvatar", column = "sender_avatar")
    })
    List<Message> getRecentMessages();
}
