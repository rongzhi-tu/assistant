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
@Table(name = "HI_LOGMIRROR_DATASOURCE")
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
    @Column(name = "request_user")
    private String requestUser;

    /**
     * 请求发送时间
     */
    @Column(name = "request_date")
    private Date requestDate;

    /**
     * 服务编码
     */
    @Column(name = "service_code")
    private String serviceCode;

    /**
     * 由中心端回写 - 服务数据内容(原生回执)
     */
    @Column(name = "service_resp")
    private String serviceResp;


    /**
     * 服务参数
     */
    @Column(name = "service_reqt")
    private String serviceReqt;


    /**
     * 接收者
     */
    @Column(name = "touser_code")
    private String touserCode;
}
