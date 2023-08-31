package com.bsoft.assistant.mapper;

import com.bsoft.assistant.model.entity.DatasourceEntity;
import ctd.persistence.support.mybatis.MybatisBaseDAO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by
 * tuz
 * on 2023/5/22.
 */
public interface DatasourceMapper extends MybatisBaseDAO<DatasourceEntity> {
    void insertDatasource(DatasourceEntity record);

    void updateDatasource(DatasourceEntity record);

    DatasourceEntity findById(String id);

    List<DatasourceEntity> findByNameFu(@Param(value = "idFu")String idFu, @Param(value = "name")String name);

    void removeById(@Param(value = "id")String id, @Param(value = "userId") String userId);
}