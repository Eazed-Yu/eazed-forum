package top.eazed.forum.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;


public interface ImageService {
    String uploadAvatar(MultipartFile file, int id) throws IOException;
    
    void fetchImageFromMinio(OutputStream outputStream, String imagePath) throws Exception;
}
