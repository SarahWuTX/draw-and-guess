package cn.sarahw.draw.utility.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputUser {
    private String email;

    private String password;

    private String name;

    private String avatar;
}
