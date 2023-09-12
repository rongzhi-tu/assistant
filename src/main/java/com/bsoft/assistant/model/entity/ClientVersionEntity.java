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
@Table(name = "SSO_ASSISTANT_CLIENT_VERSION")
@Data
public class ClientVersionEntity extends BaseModel implements Serializable {
    /**
     * 记录id
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 程序名称
     */
    @Column(name = "CLIENT_NAME")
    private String clientName;

    /**
     * 发布时间
     */
    @Column(name = "UP_TIME")
    private Date upTime;

    /**
     * 版本号
     */
    @Column(name = "VERSION")
    private String version;
}
