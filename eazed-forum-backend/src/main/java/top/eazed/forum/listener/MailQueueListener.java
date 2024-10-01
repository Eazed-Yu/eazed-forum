package top.eazed.forum.listener;

import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "mail")
public class MailQueueListener {
    
    @Resource
    private JavaMailSender sender;
    
    @Value("${spring.mail.username}")
    private String username;
    
    @RabbitHandler
    public void sendMailMessage(Map<String, Object> data) {
        String email = (String) data.get("email");
        Integer code = (Integer) data.get("code");
        String type = (String) data.get("type");
        
        SimpleMailMessage message = switch (type) {
            case "register" -> createMessage(
                    "欢迎注册",
                    email + "您好！\n" + "您的验证码为：" + code + "有效时间为3分钟。", email
            );
            case "reset" -> createMessage("重置您的密码",
                    email + "您好！\n" + "您的验证码为：" + code + "有效时间为3分钟。", email
            );
            case "modify" -> createMessage("修改您的邮箱",
                    email + "您好！\n" + "您的验证码为：" + code + "有效时间为3分钟。", email
            );
            default -> null;
        };
        if (message == null) return;
        sender.send(message);
    }
    
    private SimpleMailMessage createMessage(String title, String content, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(title);
        message.setText(content);
        message.setTo(email);
        message.setFrom(username);
        return message;
    }
}
