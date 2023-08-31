package com.bsoft.assistant.common.projectenum;

import com.bsoft.assistant.common.annotation.EnumConstantResource;

import java.util.*;

/**
 * Created by
 * tuz
 * on 2021/12/13.
 */
@EnumConstantResource(value = "YesNoEnum")
public enum YesNoEnum implements IResourceEnum<Integer, String> {
    YES(1, "是"),
    NO(0, "否");
    private Integer code;
    private String text;

    private static final Map<Integer, YesNoEnum> enums = new HashMap<>();
    static {
        enums.put(YES.getCode(), YES);
        enums.put(NO.getCode(), NO);
    }
    public static List<Map<String, Object>> enumResourceList() {
        final List<Map<String, Object>> result = new LinkedList<>();
        enums.keySet().forEach(i -> result.add(enums.get(i).transforResourceItem()));
        return result;
    }

    public static YesNoEnum parse(Integer code) {
        return enums.getOrDefault(code, null);
    }

    YesNoEnum(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    //region get set
    public Integer getCode() {
        return this.code;
    }

    public String getText() {
        return this.text;
    }
    //endregion
}
