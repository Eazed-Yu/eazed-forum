package top.eazed.forum.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import top.eazed.forum.entity.BaseData;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Data
@TableName("db_account_privacy")
public class AccountPrivacyDTO implements BaseData {
    @TableId(type = IdType.AUTO)
    final Integer id;
    boolean phone = false;
    boolean email = false;
    boolean wechat = false;
    boolean qq = false;
    boolean gender = false;
    
    public String[] hiddenFields() {
        List<String> list = new ArrayList<>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                if (field.getType().equals(boolean.class) && !field.getBoolean(this)) {
                    list.add(field.getName());
                }
            } catch (Exception ignored) {
            }
        }
        return list.toArray(String[]::new);
    }
}
