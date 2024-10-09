package top.eazed.forum.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.minio.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.eazed.forum.entity.dto.AccountDTO;
import top.eazed.forum.entity.dto.StoreImageDTO;
import top.eazed.forum.filter.FlowLimitFilter;
import top.eazed.forum.mapper.AccountMapper;
import top.eazed.forum.mapper.ImageStoreMapper;
import top.eazed.forum.service.ImageService;
import top.eazed.forum.utils.Const;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class ImageServiceImpl extends ServiceImpl<ImageStoreMapper, StoreImageDTO> implements ImageService {
    
    
    @Resource
    private MinioClient minioClient;
    
    @Resource
    private AccountMapper accountMapper;
    
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    @Resource
    private FlowLimitFilter flowLimitFilter;
    
    public String uploadAvatar(MultipartFile file, int id) throws IOException {
        String imageName = "/avatar/" + UUID.randomUUID().toString().replace("-", "");
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket("study")
                .object(imageName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .build();
        try {
            minioClient.putObject(args);
            String avatar = accountMapper.selectById(id).getAvatar();
            this.deleteOldAvatar(avatar);
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
    
    @Override
    public String uploadImage(MultipartFile file, int id) throws IOException {
        String key = Const.FORUM_IMAGE_COUNTER + id;
        if (!flowLimitFilter.limitPeriodCounterCheck(key, 100, 3600)) {
            return null;
        }
        String imageName = UUID.randomUUID().toString().replace("-", "");
        Date date = new Date();
        imageName = "/cache/" + format.format(date) + "/" + imageName;
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket("study")
                .object(imageName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .build();
        try {
            minioClient.putObject(args);
            if (!this.save(new StoreImageDTO(id, imageName, date))) {
                return null;
            }
            return imageName;
        } catch (Exception e) {
            log.error("上传头像失败{}", e.getMessage(), e);
            return null;
        }
    }
    
    
    private void deleteOldAvatar(String avatar) throws Exception {
        if (avatar == null || avatar.isEmpty()) {
            return;
        }
        RemoveObjectArgs remove = RemoveObjectArgs.builder()
                .bucket("study")
                .object(avatar)
                .build();
        minioClient.removeObject(remove);
    }
}
