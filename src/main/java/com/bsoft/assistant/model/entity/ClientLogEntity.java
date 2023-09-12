package com.bsoft.assistant.model.entity;

import com.bsoft.assistant.model.BaseModel;
import ctd.schema.annotation.Schema;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Schema
@Entity
@Table(name = "SSO_ASSISTANT_CLIENT_LOG")
@Data
public class ClientLogEntity extends BaseModel implements Serializable {
    /**
     * 记录id
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 登录用户编码
     */
    @Column(name = "user_code")
    private String userCode;

    /**
     * 登录用户ip
     */
    @Column(name = "user_ip")
    private String userIp;

    /**
     * 登录用户mac
     */
    @Column(name = "user_mac")
    private String userMac;

    /**
     * 登录的客户端版本号
     */
    @Column(name = "client_version")
    private String clientVersion;

    /**
     * 在线状态 0: 离线 1:在线 2:离开 9:忙碌
     */
    @Column(name = "online_state")
    private String onlineState;


    /**
     * 离线时间
     */
    @Column(name = "endtime")
    private Date endTime;


    /**
     * 在线时间
     */
    @Column(name = "createtime")
    private Date createtime;
}
