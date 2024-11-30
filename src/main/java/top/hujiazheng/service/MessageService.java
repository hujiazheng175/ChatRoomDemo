package top.hujiazheng.service;


import top.hujiazheng.Util.Result;

/**
 * @Author HuXiaoMing
 * @Date 2024/11/28 19:23
 * @Description
 */
public interface MessageService {
    Result sendMessage(Long senderId, String content);

    Result getHistory();

    Result getOnlineUsers();
}
