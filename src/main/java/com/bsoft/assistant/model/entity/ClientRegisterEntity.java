package com.bsoft.assistant.model.entity;

import ctd.schema.annotation.Schema;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @ClassName SsoAssistantClientTegisterEntity
 * @description: {门户小助手客户端 启用功能模块}
 * @author: fanxy
 * @create: 2023-09-02 09:31
 * @Version 1.0
 **/

@Schema
@Entity
@Table(name = "SSO_ASSISTANT_CLIENT_REGISTER")
@Data
public class ClientRegisterEntity extends BaseEntity implements Serializable {

    /**
     * 服务编码
     */
    @Column(name = "SERVER_CODE")
    private String serverCode;

    /**
     * 服务名称
     */
    @Column(name = "SERVER_NAME")
    private String serverName;

    /**
     * 返回关键字
     */
    @Column(name = "CODE")
    private String code;

    /**
     * 提醒类型
     */
    @Column(name = "TIPS_TYPE")
    private String tipsType;

    /**
     * 服务url
     */

    @Column(name = "SERVER_URL")
    private String serverUrl;


    /**
     * 服务备用url
     */
    @Column(name = "SERVER_URL_BAK")
    private String serverUrlBak;


    /**
     * 服务描述
     */
    @Column(name = "SERVER_DISCREPTION")
    private String serverDiscreption;

}
