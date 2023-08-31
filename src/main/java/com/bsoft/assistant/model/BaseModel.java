package com.bsoft.assistant.model;

import lombok.Data;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by
 * tuz
 * on 2023/5/31.
 */
@Data
public class BaseModel {

    @Transient
    private Map<String,Object> extData = new HashMap<>();
}
