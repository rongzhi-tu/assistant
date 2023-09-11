package com.bsoft.assistant.service;

import com.bsoft.assistant.mapper.ClientRegisterMapper;
import com.bsoft.assistant.mapper.ClientRegisterUserMapper;
import com.bsoft.assistant.model.entity.ClientRegisterEntity;
import com.bsoft.assistant.model.entity.ClientUserEntity;
import com.github.pagehelper.PageHelper;
import ctd.util.AppContextHolder;
import ctd.util.annotation.RpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssdev.bbp.person.IPersonService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @InterfaceName SsoAssistantClientTegisterService
 * @description: {服务注册Service层}
 * @author: fanxy
 * @create: 2023-09-02 11:04
 * @Version 1.0
 **/
@Service("ssoAssistantClientTegisterService")
public class SsoAssistantClientTegisterService {
    @Autowired
    private ClientRegisterMapper clientRegisterMapper;

    @Autowired
    private ClientRegisterUserMapper clientRegisterUserMapper;

    /**
     * @author fanxy
     * @description 保存服务
     * @createTime  2023/9/7 16:21
     * @param      params
     * @return     void
     **/
    @RpcService
    public void insertSsoAssistantClientTegister(Map<String ,Object> params){
        //SsoAssistantClientTegisterEntity ssoAssistantClientTegisterEntity = new SsoAssistantClientTegisterEntity();
//        ssoAssistantClientTegisterEntity.setServerCode(params.get("serverCode").toString());
//        ssoAssistantClientTegisterEntity.setServerName(params.get("serverName").toString());
//        ssoAssistantClientTegisterEntity.setCode(params.get("code").toString());
//        ssoAssistantClientTegisterEntity.setTipsType(params.get("tipsType").toString());
//        ssoAssistantClientTegisterEntity.setServerUrl(params.get("serverUrl").toString());
//        ssoAssistantClientTegisterEntity.setServerUrlBak(params.get("serverUrlBak").toString());
//        ssoAssistantClientTegisterEntity.setServerDiscreption(params.get("serverDiscreption").toString());

        clientRegisterMapper.insertClientRegister(params);
    }


    /**
     * @author fanxy
     * @description 根据服务名称和分页信息查询服务
     * @createTime  2023/9/7 16:21
     * @param      params
     * @return     Map<String ,Object>
     **/
    @RpcService
    public  Map<String ,Object> selectSsoAssistantClientTegister(Map<String ,Object> params){
        String serverName = params.get("serverName") == "" ? "" : params.get("serverName").toString();
        int pageNum = (int) params.get("pageNo");
        int pageSize = (int) params.get("pageSize");
        Integer total = 0;
        Map<String ,Object> result = new HashMap<>();
        PageHelper.startPage(pageNum,pageSize);
        List<ClientRegisterEntity> list = clientRegisterMapper.selectClientRegister(serverName);
        total = clientRegisterMapper.selectCountNumber(serverName);
        result.put("list",list);
        result.put("total",total);
        return result;
    }

    /**
     * @author fanxy
     * @description 修改服务
     * @createTime  2023/9/7 16:21
     * @param      params
     * @return     void
     **/
    @RpcService
    public void updateSsoAssistantClientTegister(Map<String ,Object> params){
        clientRegisterMapper.updateClientRegister(params);
    }


    /**
     * @author fanxy
     * @description 删除服务
     * @createTime  2023/9/7 16:21
     * @param      params
     * @return     void
     **/
    @RpcService
    public void deleteSsoAssistantClientTegister(Map<String ,Object> params){
        clientRegisterMapper.deleteClientRegister(params);
    }


    /**
     * @author fanxy
     * @description 根据服务配置用户信息
     * @createTime  2023/9/7 16:21
     * @param      params
     * @return     void
     **/
    @RpcService
    public void insertSsoAssistantClientTegisterUser(Map<String ,Object> params){
        /*先根据服务编码删除原来的用户  再保存新的用户列表*/
        String serverCode = params.get("serverCode") == "" ? "" : params.get("serverCode").toString();
        List<Map<String ,Object>> savemap = (List<Map<String, Object>>) params.get("data");
        clientRegisterUserMapper.deleteClientRegisterUser(serverCode);
        savemap.stream().forEach(v->{
            Map<String, Object> response = AppContextHolder.get().getBean(IPersonService.class).getByUserId(v.get("userCode")+"");
            if(response != null){
                v.put("jgbm",response.get("orgId"));
                v.put("jgmc",response.get("orgIdText"));
            }
            clientRegisterUserMapper.insertClientRegisterUser(v);
        });

    }

    /**
     * @author fanxy
     * @description 根据服务编码查询服务用户列表
     * @createTime  2023/9/7 16:25
     * @param      serverCode
     * @return List<SsoAssistantClientTegisterUserEntity>
     **/
    
    @RpcService
    public List<ClientUserEntity> selectSsoAssistantClientTegisterUser(String serverCode){
        return clientRegisterUserMapper.selectClientRegisterUser(serverCode);
    }

    /**
     * @author fanxy
     * @description 根据服务编码或者 服务名称校验服务是否存在
     * @createTime  2023/9/7 16:26
     * @param       params
     * @return Map<String ,Object>
     **/
    
    @RpcService
    public Map<String ,Object> selectServerByCode(Map<String ,Object> params){
        String option = params.get("option")+"";
        List<ClientRegisterEntity> list = clientRegisterMapper.selectServerByCode(params);
        Map<String ,Object> result =  new HashMap<>();
        if(option.equals("upd")){
            if(list != null && list.size() > 1){
                result.put("isHaveServer",true);
            }else {
                result.put("isHaveServer",false);
            }
        }else {
            if(list != null && list.size() >0){
                result.put("isHaveServer",true);
            }else {
                result.put("isHaveServer",false);
            }
        }

        return result;
    }



    /*对外提供接口*/
      /*根据服务编码serverCode 查询服务  或者查询所有服务
      * 查询具体服务：参数格式 {serverCode:'xxxxx'}
      * 查询所有服务：参数格式 {serverCode:''}
      *
      * */
    @RpcService
    public List<ClientRegisterEntity> selectServerByServerCode(Map<String ,Object> params){
        String serverCode = params.get("serverCode") == "" ? "" : params.get("serverCode").toString();
        return clientRegisterMapper.selectServerByServerCode(serverCode);
    }
}
