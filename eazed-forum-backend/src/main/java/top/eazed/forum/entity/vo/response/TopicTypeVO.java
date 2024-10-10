package top.eazed.forum.entity.vo.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicTypeVO {
    Integer id;
    String name;
    String description;
}
