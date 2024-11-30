package top.hujiazheng.exception;

import lombok.Getter;

/**
 * @Author HuXiaoMing
 * @Date 2024/11/28 19:42
 * @Description 异常处理类
 */
@Getter
public class BusinessException extends RuntimeException {
    private final Integer code;
    private final String message;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(String message) {
        this(500, message);
    }
}
