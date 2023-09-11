package com.bsoft.assistant.mapper;

import com.bsoft.assistant.model.entity.ClientRegisterEntity;
import ctd.persistence.support.mybatis.MybatisBaseDAO;
import java.util.List;
import java.util.Map;

/**
 * @InterfaceName SsoAssistantClientTegisterMapper
 * @description: {TODO}
 * @author: fanxy
 * @create: 2023-09-02 10:26
 * @Version 1.0
 **/
public interface ClientRegisterMapper extends MybatisBaseDAO<ClientRegisterEntity> {

    void insertClientRegister(Map<String, Object> record);

    void updateClientRegister(Map<String, Object> record);

    List<ClientRegisterEntity> selectClientRegister(String serverName);

    void deleteClientRegister(Map<String, Object> record);

    List<ClientRegisterEntity> selectServerByCode(Map<String, Object> record);

    List<ClientRegisterEntity> selectServerByServerCode(String serverName);

    Integer selectCountNumber(String serverName);

}
