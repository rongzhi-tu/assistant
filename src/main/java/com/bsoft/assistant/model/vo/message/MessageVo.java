package com.bsoft.assistant.model.vo.message;

import lombok.Data;
import java.io.Serializable;

@Data
public class MessageVo  implements Serializable{
    private Object props;
    private String message;

    public static MessageVo as(String message){
        return  new MessageVo() {{
            setMessage(message);
        }};
    }

    public static MessageVo as(Object props,String message){
        return  new MessageVo() {{
            setProps(props);
            setMessage(message);
        }};
    }
}
