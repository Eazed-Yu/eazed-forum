package top.eazed.forum.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.eazed.forum.entity.dto.AccountDTO;
import top.eazed.forum.mapper.AccountMapper;
import top.eazed.forum.service.ImageService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {
    
    
    @Resource
    private MinioClient minioClient;
    
    @Resource
    private AccountMapper accountMapper;
    
    public String uploadAvatar(MultipartFile file, int id) throws IOException {
        String imageName = "/avatar/" + UUID.randomUUID().toString().replace("-", "");
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket("study")
                .object(imageName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .build();
        try {
            minioClient.putObject(args);
            if (accountMapper.update(null, Wrappers.<AccountDTO>update().eq("id", id).set("avatar", imageName)) != 1) {
                return null;
            }
        } catch (Exception e) {
            log.error("上传头像失败{}", e.getMessage(), e);
        }
        return imageName;
    }
    
    @Override
    public void fetchImageFromMinio(OutputStream outputStream, String imagePath) throws Exception {
        GetObjectArgs args = GetObjectArgs.builder()
                .bucket("study")
                .object(imagePath)
                .build();
        GetObjectResponse response = minioClient.getObject(args);
        IOUtils.copy(response, outputStream);
    }
}
