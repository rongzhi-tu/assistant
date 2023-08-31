package com.bsoft.assistant.model.entity;

import ctd.schema.annotation.Schema;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Schema
@Entity
@Table(name = "HI_LOGMIRROR_DATASOURCE")
@Data
public class DatasourceEntity extends BaseEntity implements Serializable {

    /**
     * 租户ID
     */
    @Column(name = "DB_NAME")
    private String dbName;

    /**
     * 分类编码
     */
    @Column(name = "DICTIONARY_PATH")
    private String dictionaryPath;

    /**
     * 服务器信息
     */
    @Column(name = "DB_HOST")
    private String dbHost;

    /**
     * 端口信息
     */
    @Column(name = "DB_PORT")
    private String dbPort;

    /**
     * 用户
     */
    @Column(name = "DB_USER")
    private String dbUser;


    /**
     * 密码
     */
    @Column(name = "DB_PASSWORD")
    private String dbPassword;


    /**
     * 数据源空间
     */
    @Column(name = "DB_TABLESPACE")
    private String dbTablespace;


    /**
     * 服务名
     */
    @Column(name = "DB_SERVICE_NAME")
    private String dbServiceName;

    /**
     * 数据库类型: mysql、oracle、sql server  可按 DBTypeEnum
     */
    @Column(name = "DB_TYPE")
    private String dbType;

    /**
     * 分类ID
     */
    @Column(name = "ID_DATASOURCE_FU")
    private String idDatasourceFu;
}
