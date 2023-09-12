package com.bsoft.assistant.mapper;

import com.bsoft.assistant.model.entity.TomessageLogEntity;
import ctd.persistence.support.mybatis.MybatisBaseDAO;

import java.util.List;

public interface TomessageLogMapper extends MybatisBaseDAO<TomessageLogEntity> {
    void insert(TomessageLogEntity tomessageLogEntity);

    void batchInsert(List<TomessageLogEntity> tomessageLogs);

    List<TomessageLogEntity> selectAllByUserCode(String touserCode);

    List<TomessageLogEntity> selectNotReciveByUserCode(String touserCode);

    void updateReciveItemById(Long id);
}
