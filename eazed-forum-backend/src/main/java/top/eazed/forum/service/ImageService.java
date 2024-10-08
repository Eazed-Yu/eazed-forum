package top.eazed.forum.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import top.eazed.forum.entity.dto.StoreImageDTO;

import java.io.IOException;
import java.io.OutputStream;


public interface ImageService extends IService<StoreImageDTO> {
    String uploadAvatar(MultipartFile file, int id) throws IOException;
    void fetchImageFromMinio(OutputStream outputStream, String imagePath) throws Exception;
    
    String uploadImage(MultipartFile file, int id) throws IOException;
}
