package top.hujiazheng.service.impl;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.hujiazheng.Util.JwtUtil;
import top.hujiazheng.Util.PasswordUtil;
import top.hujiazheng.Util.Result;
import top.hujiazheng.mapper.UserMapper;
import top.hujiazheng.model.DTO.UserDTO;
import top.hujiazheng.model.entity.User;
import top.hujiazheng.service.UserService;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Author HuXiaoMing
 * @Date 2024/11/28 20:04
 * @Description
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 项目启动时，创建上传目录
     */
    @PostConstruct
    public void init() {
        // 确保上传目录存在
        try {
            // 获取项目根目录
            String projectRoot = System.getProperty("user.dir");
            // 创建上传目录
            File uploadDir = new File(projectRoot + "/src/main/resources/static/assets/useravatar");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            log.info("上传目录创建成功：{}", uploadDir.getAbsolutePath());
        } catch (Exception e) {
            log.error("创建上传目录失败", e);
        }
    }

    /**
     * 注册
     *
     * @param username
     * @param password
     * @param avatar
     * @return
     */
    @Override
    public Result register(String username, String password, MultipartFile avatar) {
        // 检查用户名是否已存在
        if (userMapper.findByUsername(username) != null) {
            return Result.error("用户名已存在");
        }

        try {
            // 生成文件名
            String fileName = UUID.randomUUID()
                    + avatar.getOriginalFilename().substring(avatar.getOriginalFilename().lastIndexOf("."));

            // 获取项目根目录
            String projectRoot = System.getProperty("user.dir");
            // 构建文件保存路径
            String avatarPath = projectRoot + "/src/main/resources/static/assets/useravatar/" + fileName;

            // 保存文件
            File dest = new File(avatarPath);
            avatar.transferTo(dest);

            // 创建用户对象
            User user = new User();
            user.setUsername(username);
            user.setPassword(PasswordUtil.encode(password));
            // 设置头像URL为相对路径
            user.setAvatarUrl("/assets/useravatar/" + fileName);
            user.setCreatedAt(new Date());
            user.setLastActive(new Date());
            user.setStatus(1);

            // 保存用户信息
            userMapper.insert(user);

            return Result.success("注册成功");
        } catch (Exception e) {
            log.error("注册失败", e);
            return Result.error("注册失败：" + e.getMessage());
        }
    }

    /**
     * 登录
     *
     * @param username
     * @param password
     * @return
     */
    @Override
    public Result login(String username, String password) {
        // 查找用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 验证密码
        if (!PasswordUtil.matches(password, user.getPassword())) {
            return Result.error("密码错误");
        }

        // 更新用户状态
        user.setStatus(1);  // 设置为在线
        user.setLastActive(new Date());
        userMapper.updateStatus(user.getId(), 1);
        userMapper.updateLastActive(user.getId());

        // 生成token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());


        // 构建返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", UserDTO.fromUser(user));

        return Result.success(data);
    }
}

