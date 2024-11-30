package top.hujiazheng.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author HuXiaoMing
 * @Date 2024/11/28 18:56
 * @Description 用户类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private String username;// 用户名
    private String password;// 密码
    private String avatarUrl;// 头像
    private Date createdAt;// 创建时间
    private Date lastActive;// 上次活跃时间
    private Integer status;  // 账号状态 1-在线，0-下线
}
