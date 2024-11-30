package top.hujiazheng.exception;

import lombok.Getter;

/**
 * @Author HuXiaoMing
 * @Date 2024/11/28 19:52
 * @Description 文件异常类
 */
@Getter
public class FileException extends BusinessException {
    public FileException(String message) {
        super(400, message);
    }
}
