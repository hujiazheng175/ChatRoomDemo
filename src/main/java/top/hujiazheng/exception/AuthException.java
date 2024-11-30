package top.hujiazheng.exception;

import lombok.Getter;

/**
 * @Author HuXiaoMing
 * @Date 2024/11/28 19:51
 * @Description 自定义异常类
 */
@Getter
public class AuthException extends BusinessException {
    public AuthException(String message) {
        super(401, message);
    }
}
