package com.bsoft.assistant.utils;

import ctd.account.UserRoleToken;
import ctd.account.tenant.controller.TenantController;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryItem;
import ctd.dictionary.controller.DictionaryController;
import ctd.dictionary.entity.DictionaryItemEntity;
import ctd.net.rpc.util.ServiceAdapter;
import ctd.organize.Department;
import ctd.organize.entity.DepartmentEntity;
import ctd.parameter.Parameter;
import ctd.parameter.controller.ParameterController;
import ctd.parameter.entity.ParameterEntity;
import ctd.util.JSONUtils;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公共方法
 */
public class SystemParamUtils {

    /**
     * 判断对象是否为空
     *
     * @param obj 判断对象
     * @return true:空 false:不为空
     */
    public static boolean isEmpty(Object obj) {
        return !isNotEmpty(obj);
    }

    /**
     * 判断对象是否不为空
     *
     * @param obj 判断对象
     * @return true:不为空 false:空
     */
    public static boolean isNotEmpty(Object obj) {
        if (obj != null && !"".equals(obj.toString().trim())) {
            return true;
        }
        return false;
    }

    /**
     * 根据字典编码和字典项编码获取字典项
     *
     * @param sdId  字典编码
     * @param cd 字典项编码
     * @return DictionaryItemEntity
     * @throws ControllerException
     */
    public static DictionaryItemEntity getDictionaryItem(String sdId, String cd) {
        try {
            Dictionary dic = DictionaryController.instance().get(sdId);
            DictionaryItem di = dic.getItem(cd);
            return (DictionaryItemEntity) di;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static DictionaryItem getDictionaryItems(String sdId, String cd) {
        try {
            Dictionary dic = DictionaryController.instance().get(sdId);
            DictionaryItem di = dic.getItem(cd);
            return (DictionaryItem) di;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据字典编码和字典项编码获取字典项ID
     *
     * @param sdId  字典编码
     * @param cd 字典项编码
     * @return String
     * @throws ControllerException
     */
    public static String getDictionaryItemId(String sdId, String cd) {
        try {
            return getDictionaryItem(sdId, cd).getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDictionaryItemIdIfNull(String sdId, String cd) {
        try {
            return StringUtils.isEmpty(cd)?"":getDictionaryItem(sdId, cd).getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据字典编码和字典项编码获取字典项Text
     *
     * @param sdId  字典编码
     * @param cd 字典项编码
     * @return String
     * @throws ControllerException
     */
    public static String getDictionaryItemText(String sdId, String cd) {
        try {
            return getDictionaryItem(sdId, cd).getNa();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDictionaryItemTexts(String sdId, String cd) {
        try {
            return getDictionaryItems(sdId, cd).getText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 当前登录人的组织机构
     *
     * @return String 组织机构ID
     * @throws Exception
     */
    public static String getUserOrgId() {
        String orgId = "";
        try {
            orgId = UserRoleToken.getCurrent().getOrgId();
            if (isEmpty(orgId)) {
                orgId = UserRoleToken.getCurrent().getManageUnit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orgId;
    }

    /**
     * 当前登录人的组织机构名称
     *
     * @return String 组织机构名称
     */
    public static String getUserOrgText() {
        try {
            String userId = UserRoleToken.getCurrent().getUserId();

            Map<String, Object> map = (Map) ServiceAdapter.invoke("bbp.person", "getByUserId", userId);

            return (String) map.get("orgIdText");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 当前登录人的ID
     *
     * @return String 登录人的ID
     * @throws Exception
     */
    public static String getUserId() {
        String userId = "";
        try {
            userId = UserRoleToken.getCurrent().getUserId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userId;
    }

    /**
     * 当前登录人的ID
     *
     * @return String 登录人的ID
     * @throws Exception
     */
    public static String getUserName() {
        String userName = "";
        try {
            userName = UserRoleToken.getCurrent().getUserName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userName;
    }

    /**
     * 获取数据账户信息
     *
     * @return Map<String, Object> 数据账户
     * @throws Exception
     */
    public static Map<String, Object> getBasicData() {
        try {
            String orgId = getUserOrgId();

            return (Map<String, Object>) ServiceAdapter.invoke("bbp.basicData", "getByOrgId", orgId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap();
    }

    /**
     * 当前登录人的数据账户
     *
     * @return String 数据账户ID
     * @throws Exception
     */
    public static String getIdBdmd() {
        try {
            Map<String, Object> map = getBasicData();
            if (map != null && map.size() > 0) {
                return (String) map.get("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 当前登录人的数据账户
     *
     * @return String 数据账户ID
     * @throws Exception
     */
    public static String getIdBdmdNa() {
        try {
            Map<String, Object> map = getBasicData();
            if (map != null && map.size() > 0) {
                return (String) map.get("na");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 当前登录人的租户ID
     *
     * @return String 租户ID
     * @throws Exception
     */
    public static String getIdTet() {
        String idTet;
        try {
            idTet = UserRoleToken.getCurrent().getTenantId();
            return idTet;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前登录用户的部门ID
     * @return String
     */
    public static String getUserDeptId() {
        try {
            return (String) ContextUtils.get(Context.CURRENT_DEPT_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取当前登录用户的部门
     * @return DepartmentEntity
     */
    public static DepartmentEntity getUserDept() {
        try {
            String deptId = getUserDeptId();

            Department department = (Department) TenantController.instance().lookupCurrentManageUnit(deptId);
            DepartmentEntity dept = JSONUtils.parse(JSONUtils.toString(department), DepartmentEntity.class);

            return dept;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取当前登录用户的部门名称
     * @return String
     */
    public static String getUserDeptName() {
        try {
            DepartmentEntity dept = getUserDept();
            if (isNotEmpty(dept)) {
                return dept.getCd();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 根据部门业务线编码和租户id获取部门业务线关系
     *
     * @param cd 部门业务线编码
     * @param tenantId 租户id
     * @return List
     */
    public static List findServiceLineLink(String cd, String tenantId) {
        try {
            return (List) ServiceAdapter.invoke("bbp.department", "findServiceLineLink", cd, tenantId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 根据编码获取系统参数
     *
     * @param cd 编码
     * @return ParameterEntity
     */
    public static ParameterEntity getSysParamByCd(String cd) {
        try {
            if (StringUtils.isEmpty(cd)) {
                return null;
            }
            return (ParameterEntity) ParameterController.instance().getParameterByCode(cd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据编码获取系统参数的默认值
     *
     * @param cd 编码
     * @return String
     */
    public static String getSysParamDefaultValueByCd(String cd) {
        try {
            return getSysParamByCd(cd).getDefaultValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void getSystemParamByCd(String code){
        try{
            if(StringUtils.isEmpty(code)){
                //return null;
            }
            Parameter parameter = ParameterController.instance().getParameterByCode(code);
            System.out.println(parameter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SystemParamUtils.getSystemParamByCd("SFPCGL");
    }
}
