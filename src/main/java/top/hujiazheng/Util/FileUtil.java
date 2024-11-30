package top.hujiazheng.Util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 文件上传工具类
 */
@Component
public class FileUtil {

    @Value("${upload.path}")
    private String uploadPath;

    public String saveFile(MultipartFile file) throws IOException {
        // 确保目录存在
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID() + extension;

        // 保存文件
        Path path = Paths.get(uploadPath, newFilename);
        Files.copy(file.getInputStream(), path);

        return newFilename;
    }

    public void deleteFile(String filename) throws IOException {
        Path path = Paths.get(uploadPath, filename);
        Files.deleteIfExists(path);
    }
}
