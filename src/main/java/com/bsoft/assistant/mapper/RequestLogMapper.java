package com.bsoft.assistant.mapper;

import com.bsoft.assistant.model.entity.RequestLogEntity;
import ctd.persistence.support.mybatis.MybatisBaseDAO;
import java.util.Map;

public interface RequestLogMapper extends MybatisBaseDAO<RequestLogEntity> {
    void insert(RequestLogEntity requestLogEntity);

    void updateRespById(Map<String, Object> record);
}
