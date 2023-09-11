package com.bsoft.assistant.model.vo.message;

import lombok.Data;
import java.io.Serializable;

@Data
public class MessageResult implements Serializable {
    private Object data;

    private int status;

    private Object extMsg;

    public static MessageResult successResult(Object data){
        return new MessageResult(){{
            setData(data);
            setStatus(200);
        }};
    }
    public static MessageResult successResult(Object data,Object extMsg){
        return new MessageResult(){{
            setData(data);
            setStatus(200);
            setExtMsg(extMsg);
        }};
    }
    public static MessageResult failResult(Object extMsg){
        return new MessageResult(){{
            setExtMsg(extMsg);
            setStatus(501);
        }};
    }

    public static MessageResult failResult(){
        return new MessageResult(){{
            setStatus(501);
        }};
    }
}
