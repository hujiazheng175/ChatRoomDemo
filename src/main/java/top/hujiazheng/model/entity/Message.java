package top.hujiazheng.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author HuXiaoMing
 * @Date 2024/11/28 18:58
 * @Description 消息存储实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private Long id; // 消息ID
    private Integer senderId;// 发送者ID
    private String content; // 消息内容
    private Date createdAt;// 发送时间

    // 用于前端展示的额外字段
    private String senderName; // 发送者名称
    private String senderAvatar;// 发送者头像
}
