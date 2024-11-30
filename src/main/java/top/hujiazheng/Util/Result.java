package top.hujiazheng.Util;

import lombok.Data;

/**
 * @Author HuXiaoMing
 * @Date 2024/11/29 10:05
 * @Description 统一返回结果
 */
@Data
public class Result {
    private boolean success;
    private String message;
    private Object data;

    private Result(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static Result success(Object data) {
        return new Result(true, "操作成功", data);
    }

    public static Result success(String message) {
        return new Result(true, message, null);
    }

    public static Result success(String message, Object data) {
        return new Result(true, message, data);
    }

    public static Result error(String message) {
        return new Result(false, message, null);
    }
}
