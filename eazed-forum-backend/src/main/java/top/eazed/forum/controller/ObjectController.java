package top.eazed.forum.controller;


import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.eazed.forum.entity.RestBean;
import top.eazed.forum.service.ImageService;


@RestController
public class ObjectController {
    
    @Resource
    ImageService imageService;
    
    @GetMapping("/images/**")
    public void imageFetch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        fetchImage(request, response);
    }
    
    private void fetchImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String imagePath = request.getServletPath().substring(7);
        
        ServletOutputStream outputStream = response.getOutputStream();
        if (imagePath.length() <= 13) {
            outputStream.println(RestBean.failure(404, "not found").toString());
        } else {
            imageService.fetchImageFromMinio(outputStream, imagePath);
            response.setHeader("Cache-Control", "max-age=31536000");
            response.setHeader("Content-Type", "image/jpeg");
        }
    }
}
