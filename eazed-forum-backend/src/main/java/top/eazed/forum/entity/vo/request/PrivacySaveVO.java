package top.eazed.forum.entity.vo.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PrivacySaveVO {
    @Pattern(regexp = "(phone|email|qq|wechat|gender)")
    String type;
    boolean status;
}
