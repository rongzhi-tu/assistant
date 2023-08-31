package com.bsoft.assistant.common.utils;


import org.apache.commons.beanutils.BeanUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 获取到的List<Map>结果集转化为JavaBean工具类
 *
 *
 */
public class BIBeanUtils<T> {

    public static final BIBeanUtils INSTANCE = new BIBeanUtils();
    /**
     * 根据List<Map<String, Object>>数据转换为JavaBean数据
     *
     * @param datas
     * @param beanClass
     * @return
     * @throws CommonException
     */
    public List<T> ListMap2JavaBean(List<Map<String, Object>> datas, Class<T> beanClass) throws CommonException {
        // 返回数据集合
        List<T> list = null;
        // 对象字段名称
        String fieldname = "";
        // 对象方法名称
        String methodname = "";
        // 对象方法需要赋的值
        Object methodsetvalue = "";
        try {
            list = new ArrayList<T>();
            // 得到对象所有字段
            Field fields[] = beanClass.getDeclaredFields();
            // 遍历数据
            for (Map<String, Object> mapdata : datas) {
                // 创建一个泛型类型实例
                T t = beanClass.newInstance();
                // 遍历所有字段，对应配置好的字段并赋值
                for (Field field : fields) {
                    if (null != field) {
                        if ("serialVersionUID".equals(field.getName())) {
                            continue;
                        }
                        // 全部转化为大写
                        String dbfieldname = field.getName().toUpperCase();
                        // 获取字段名称
                        fieldname = field.getName();
                        // 拼接set方法
                        methodname = "set" + StrUtil.capitalize(fieldname);
                        // 获取data里的对应值
                        methodsetvalue = mapdata.get(dbfieldname);
                        // 赋值给字段
                        Method m = beanClass.getDeclaredMethod(methodname, field.getType());
                        m.invoke(t, methodsetvalue);
                    }
                }
                // 存入返回列表
                list.add(t);
            }
        } catch (InstantiationException e) {
            throw new CommonException(e, "创建beanClass实例异常");
        } catch (IllegalAccessException e) {
            throw new CommonException(e, "创建beanClass实例异常");
        } catch (SecurityException e) {
            throw new CommonException(e, "获取[" + fieldname + "] getter setter 方法异常");
        } catch (NoSuchMethodException e) {
            throw new CommonException(e, "获取[" + fieldname + "] getter setter 方法异常");
        } catch (IllegalArgumentException e) {
            throw new CommonException(e, "[" + methodname + "] 方法赋值异常");
        } catch (InvocationTargetException e) {
            throw new CommonException(e, "[" + methodname + "] 方法赋值异常");
        }
        // 返回
        return list;
    }

    /**
     * 循环遍历MapList，组成前端需要的Map数据载体
     *
     * @param datas
     * @return
     */
    public List<String> ListMap2StringArray(List<Map<String, Object>> datas, String as) {
        try {
            List<String> resultList = new ArrayList<>();
            // 遍历数据
            for (Map<String, Object> mapdata : datas) {
                // 获取data里的对应值
                Object methodsetvalue = mapdata.get(as);
                if (null != methodsetvalue) {
                    resultList.add(String.valueOf(methodsetvalue));
                }
            }
            return resultList;
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T convertMap2Bean(Map map, Class T) throws Exception {
        if(map==null || map.size()==0){
            return null;
        }
        BeanInfo beanInfo = Introspector.getBeanInfo(T);
        T bean = (T)T.newInstance();
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (int i = 0, n = propertyDescriptors.length; i <n ; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            String upperPropertyName  = propertyName.toUpperCase();
            if (map.containsKey(upperPropertyName)) {
                Object value = map.get(upperPropertyName);
                BeanUtils.copyProperty(bean, propertyName, value);
//                BeanUtils.copyProperties();

            }
        }
        return bean;
    }


    public static <T> List<T> convertListMap2ListBean(List<Map<String,Object>> listMap, Class T) throws Exception {
        List<T> beanList = new ArrayList<T>();
        for(int i=0, n=listMap.size(); i<n; i++){
            Map<String,Object> map = listMap.get(i);
            T bean = convertMap2Bean(map,T);
            beanList.add(bean);
        }
        return beanList;
    }

}
