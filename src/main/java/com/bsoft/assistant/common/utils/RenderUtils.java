package com.bsoft.assistant.common.utils;


import com.bsoft.assistant.common.function.ResourceRenderFunction;
import com.bsoft.assistant.model.BaseModel;
import com.bsoft.assistant.common.projectenum.IResourceEnum;
import ctd.account.tenant.controller.TenantController;
import ctd.controller.exception.ControllerException;
import ctd.organize.entity.OrganizationEntity;
import ctd.util.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by
 * tuz
 * on 2021/12/17.
 * 主要用于列表或单项的数据模型相关的数据渲染工具类
 */
public class RenderUtils {

    //region 枚举model数据渲染
    /**
     * 数据源按枚举列表解析到BaseModel中
     *
     * @param data：数据源
     * @param fieldName：待解析的属性名, 解析后的名称放在aseModel的extData中
     * @param resourceRenderFunction：解析项的算法
     */
    public static void enumRenders(List<? extends BaseModel> data, final String fieldName, ResourceRenderFunction resourceRenderFunction) {
        enumRenders(data, fieldName, fieldName, resourceRenderFunction);
    }

    /**
     * 数据源按枚举列表解析到BaseModel中
     *
     * @param data：数据源
     * @param fieldName：待解析的属性名
     * @param extFieldName：存放的新名称
     * @param resourceRenderFunction：解析项的算法
     */
    public static void enumRenders(List<? extends BaseModel> data, final String fieldName, final String extFieldName, ResourceRenderFunction resourceRenderFunction) {
        if (!CollectionUtils.isEmpty(data)) {
            data.forEach(i -> enumRender(i, fieldName, extFieldName, resourceRenderFunction));
        }
    }

    public static void enumRender(BaseModel model, String fieldName, ResourceRenderFunction resourceRenderFunction) {
        enumRender(model,fieldName,fieldName,resourceRenderFunction);
    }
    /**
     * 数据源按枚举列表解析到BaseModel中
     *
     * @param model：数据源
     * @param fieldName：待解析的属性名
     * @param extFieldName：存放的新名称
     * @param resourceRenderFunction：解析项的算法
     */
    public static void enumRender(BaseModel model, String fieldName, String extFieldName, ResourceRenderFunction resourceRenderFunction) {
        model.getExtData().put(extFieldName, null);
        IResourceEnum resourceEnum = resourceRenderFunction.parse(BeanUtils.getProperty(model, fieldName));
        if (resourceEnum != null) {
            model.getExtData().put(extFieldName, resourceEnum.getText());
        }
    }
    //endregion

    //region 机构model数据渲染
    //单条渲染
    public static void orgRender(BaseModel model, String fieldName, String extFieldName) throws ControllerException {
        model.getExtData().put(extFieldName, null);
        OrganizationEntity organizationEntity = (OrganizationEntity) TenantController.instance().lookupCurrentManageUnit(BeanUtils.getProperty(model, fieldName).toString());
        if (organizationEntity != null) {
            model.getExtData().put(extFieldName, organizationEntity.getName());
        }
    }
    //列表渲染
    public static void orgRenders(List<? extends BaseModel> data, String fieldName, String extFieldName) throws ControllerException {
        if (!CollectionUtils.isEmpty(data)) {
            for (BaseModel i :data) {
                orgRender(i, fieldName, extFieldName);
            }
        }
    }
    //endregion
}
