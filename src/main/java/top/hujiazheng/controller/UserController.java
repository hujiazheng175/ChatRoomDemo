package top.hujiazheng.controller;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.hujiazheng.Util.Result;
import top.hujiazheng.model.DTO.LoginRequest;
import top.hujiazheng.service.UserService;

import java.io.File;

/**
 * @Author HuXiaoMing
 * @Date 2024/11/29 9:16
 * @Description 用户控制器（注册，登录）
 */
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Value("${upload.path}")
    private String uploadPath;

    /**
     * 初始化上传目录
     */
    @PostConstruct
    public void init() {
        // 确保上传目录存在
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result register(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam("avatar") MultipartFile avatar) {
        try {
            // 验证文件类型
            String contentType = avatar.getContentType();
            if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
                return Result.error("只支持JPG和PNG格式的图片");
            }

            // 验证文件大小
            if (avatar.getSize() > 5 * 1024 * 1024) {
                return Result.error("图片大小不能超过5MB");
            }

            return userService.register(username, password, avatar);
        } catch (Exception e) {
            log.error("注册失败", e);
            return Result.error("注册失败：" + e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest request) {
        try {
            return userService.login(request.getUsername(), request.getPassword());
        } catch (Exception e) {
            log.error("登录失败", e);
            return Result.error("登录失败：" + e.getMessage());
        }
    }
}
