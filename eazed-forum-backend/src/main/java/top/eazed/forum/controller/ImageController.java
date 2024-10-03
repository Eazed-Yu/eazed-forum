package top.eazed.forum.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.eazed.forum.entity.RestBean;
import top.eazed.forum.service.ImageService;
import top.eazed.forum.utils.Const;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/image")
public class ImageController {
    
    @Resource
    private ImageService imageService;
    
    @PostMapping("/avatar")
    public RestBean<String> uploadAvatar(@RequestParam("file") MultipartFile file,
                                         @RequestAttribute(Const.ATTR_USER_ID) int id) throws IOException {
        if (file.getSize() > 1024 * 1024 * 2) {
            return RestBean.failure(400, "图片大小不能大于 2M");
        }
        String url = imageService.uploadAvatar(file, id);
        if (url == null) {
            return RestBean.failure(500, "d");
        }
        log.info("用户{}上传头像{}", id, url);
        return RestBean.success(url);
    }
    
}
