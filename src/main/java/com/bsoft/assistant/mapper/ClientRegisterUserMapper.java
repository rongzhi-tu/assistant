package com.bsoft.assistant.mapper;

import com.bsoft.assistant.model.entity.ClientUserEntity;
import ctd.persistence.support.mybatis.MybatisBaseDAO;
import java.util.List;
import java.util.Map;

/**
 * @InterfaceName SsoAssistantClientTegisterUserMapper
 * @description: {TODO}
 * @author: fanxy
 * @create: 2023-09-02 10:27
 * @Version 1.0
 **/
public interface ClientRegisterUserMapper  extends MybatisBaseDAO<ClientUserEntity> {

    void insertClientRegisterUser(Map<String, Object> params);

    void updateClientRegisterUser(Map<String, Object> params);

    List<ClientUserEntity> selectClientRegisterUser(String serverCode);

    void deleteClientRegisterUser(String serverCode);
}
