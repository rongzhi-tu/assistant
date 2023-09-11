package com.bsoft.assistant.model.entity;

import ctd.schema.annotation.Schema;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @ClassName SsoAssistantClientTegisterEntity
 * @description: {门户小助手客户端 服务用户}
 * @author: fanxy
 * @create: 2023-09-02 09:31
 * @Version 1.0
 **/

@Schema
@Entity
@Table(name = "SSO_ASSISTANT_CLIENT_USER")
@Data
public class ClientUserEntity extends BaseEntity implements Serializable {

    /**
     * 服务编码
     */
    @Column(name = "SERVER_CODE")
    private String serverCode;

    /**
     * 用户编码
     */
    @Column(name = "USER_CODE")
    private String userCode;

    /**
     * 用户名称
     */
    @Column(name = "USER_NAME")
    private String userName;

    /**
     * 接入机构编码
     */
    @Column(name = "JGBM")
    private String jgbm;

    /**
     * 接入机构名称
     */
    @Column(name = "JGMC")
    private String jgmc;


}
