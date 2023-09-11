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
public class TomessageLogEntity extends BaseModel implements Serializable {
    /**
     * 记录id
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 接受者编码
     */
    @Column(name = "touser_code")
    private String touserCode;

    /**
     * 接收的信息标题 (改造后的回执)
     */
    @Column(name = "toservice_title")
    private String toserviceTitle;

    /**
     * 接收的信息内容 (改造后的回执)
     */
    @Column(name = "toservice_resp")
    private String toserviceResp;

    /**
     * 消息类型 - 根据类型执行客户端不同操作
     */
    @Column(name = "TIPS_TYPE")
    private String tipsType;

    /**
     * 查看状态 - 0:未查看 1:已查看 初始0
     */
    @Column(name = "state")
    private String state;


    /**
     * 读取消息时间
     */
    @Column(name = "request_time")
    private Date request_time;
}
