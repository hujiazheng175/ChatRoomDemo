package top.hujiazheng.model.DTO;

import lombok.Data;
import top.hujiazheng.model.entity.User;

/**
 * @Author HuXiaoMing
 * @Date 2024/11/29 9:45
 * @Description 用户传输对象
 */
@Data
public class UserDTO {
    private Long id;
    private String username;
    private String avatarUrl;

    public static UserDTO fromUser(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setAvatarUrl(user.getAvatarUrl());
        return dto;
    }
}
