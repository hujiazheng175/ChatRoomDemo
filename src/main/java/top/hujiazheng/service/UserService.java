package top.hujiazheng.service;

import org.springframework.web.multipart.MultipartFile;
import top.hujiazheng.Util.Result;

public interface UserService {
    // 注册
    Result register(String username, String password, MultipartFile avatar);

    // 登录
    Result login(String username, String password);
}
