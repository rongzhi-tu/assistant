package com.bsoft.assistant.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Created by
 * tuz
 * on 2021/12/16.
 *
 * 枚举静态数据，项目启动时将扫描解析保存，前端通过懒加载方式可直接访问
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumConstantResource {
    /**
     * 必填，当lazy为true时  可以通过<enumName>(1,2) 这种方式传参数，注意参数只支持字串  loadSql为必填且要为有效的sql语句
     *      当为false时，直接获取value标识的enum.enumResourceList进行获取
     */
    String value() default "";

    /**
     * 静态enum对应应的静态加载方法
     * @return ： enumResourceList
     */
    String loadMethodName() default  "enumResourceList";

    /**
     * @return : true时可以通过sql进行查询数据
     */
    boolean lazy() default false;

    String datasourceId() default "dataSource";
    /**
     * 动态加载数据的基础sql语句
     */
    String loadSql() default "";

    /**
     * 是否是树开数据加载
     */
    boolean treeModel() default false;

    /**
     * 树形数据时，对应字段名称
     */
    String treeModelField() default "";

    String orderByStr() default "";
}
