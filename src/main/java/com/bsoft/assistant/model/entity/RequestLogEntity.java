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
@Table(name = "SSO_ASSISTANT_REQUEST_LOG")
@Data
public class RequestLogEntity extends BaseModel implements Serializable {
    /**
     * 记录id
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 请求发送方的程序或系统名
     */
    @Column(name = "REQUEST_CLIENT")
    private String requestClient;

    /**
     * 请求发送方的用户
     */
    @Column(name = "REQUEST_USER")
    private String requestUser;

    /**
     * 请求发送时间
     */
    @Column(name = "REQUEST_DATE")
    private Date requestDate;

    /**
     * 服务编码
     */
    @Column(name = "SERVICE_CODE")
    private String serviceCode;

    /**
     * 由中心端回写 - 服务数据内容(原生回执)
     */
    @Column(name = "SERVICE_RESP")
    private String serviceResp;

    /**
     * 服务参数
     */
    @Column(name = "SERVICE_REQT")
    private String serviceReqt;

    /**
     * 接收者
     */
    @Column(name = "TOUSER_CODE")
    private String touserCode;
}
