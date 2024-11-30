package top.hujiazheng.model.DTO;

import lombok.Data;

/**
 * @Author HuXiaoMing
 * @Date 2024/11/29 10:15
 * @Description 请求登录数据传输类
 */
@Data
public class LoginRequest {
    private String username;
    private String password;
}
