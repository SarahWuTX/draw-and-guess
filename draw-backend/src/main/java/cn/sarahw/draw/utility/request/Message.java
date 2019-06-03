package cn.sarahw.draw.utility.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {
    private Object sender;

    private String message;
}
