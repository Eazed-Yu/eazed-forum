package top.eazed.forum.entity.vo.request;

import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicCreateVO {
    @Min(1)
    int type;
    @Length(min = 1, max = 30)
    String title;
    JSONObject content;
}
