package com.bsoft.assistant.common.utils;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.transform.BasicTransformerAdapter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库结果集转换
 */
public class BeanTransformer extends BasicTransformerAdapter {

    private static final long serialVersionUID = -6969415367519470712L;

    private Class target;

    private Map<String, String> fieldMap;


    public BeanTransformer(Class target) {
        this.target = target;
    }


    /**
     * tuple查询出来的值
     * aliases获取数据库查询的列值
     */
    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        Object result = null;
        try {
            result = target.newInstance();
            if (fieldMap == null) {
                fieldMap = new HashMap<String, String>();
                Class supClz = result.getClass().getSuperclass();
                do {
                    Field[] superFields = supClz.getDeclaredFields();
                    for (Field field : superFields) {
                        if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers()))
                            continue;
                        String name = field.getName();
                        fieldMap.put(name.toUpperCase(), name);
                    }
                    supClz = supClz.getSuperclass();
                } while (supClz != null);
                Field[] fields = result.getClass().getDeclaredFields();
                for (Field field : fields) {
                    String name = field.getName();
                    fieldMap.put(name.toUpperCase(), name);
                }
            }

            for (int i = 0; i < aliases.length; i++) {
                if (tuple[i] == null)
                    continue;
                try {
                    String alias = aliases[i].indexOf("_") != -1 ? aliases[i].replaceAll("_", "").toUpperCase() : aliases[i].toUpperCase();
                    if (fieldMap.containsKey(alias)) {
                        BeanUtils.setProperty(result, fieldMap.get(alias), tuple[i]);
                    }

                } catch (InvocationTargetException e) {

                }
            }

        } catch (InstantiationException e) {

        } catch (IllegalAccessException e) {

        }
        return result;
    }

    @Override
    public List transformList(List list) {
        return super.transformList(list);
    }


}
