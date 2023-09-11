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
    @Column(name = "client_name")
    private String clientName;

    /**
     * 发布时间
     */
    @Column(name = "up_time")
    private Date upTime;

    /**
     * 版本号
     */
    @Column(name = "version")
    private String version;
}
