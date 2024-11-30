package top.hujiazheng.mapper;

import org.apache.ibatis.annotations.*;
import top.hujiazheng.model.entity.User;

import java.util.List;

@Mapper
public interface UserMapper {
    @Insert("INSERT INTO users (username, password, avatar_url, created_at, last_active, status) "
            + "VALUES (#{username}, #{password}, #{avatarUrl}, #{createdAt}, #{lastActive}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);

    @Update("UPDATE users SET last_active = CURRENT_TIMESTAMP WHERE id = #{userId}")
    void updateLastActive(Long userId);

    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Long id);

    @Select("SELECT * FROM users WHERE status = 1 "
            + "AND last_active >= DATE_SUB(NOW(), INTERVAL 2 MINUTE) "  // 缩短时间窗口
            + "ORDER BY last_active DESC")
    List<User> findRecentActiveUsers();

    @Update("UPDATE users SET status = #{status} WHERE id = #{userId}")
    void updateStatus(@Param("userId") Long userId, @Param("status") Integer status);
}
