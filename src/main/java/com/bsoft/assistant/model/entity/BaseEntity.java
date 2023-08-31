package com.bsoft.assistant.model.entity;
import com.bsoft.assistant.model.BaseModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by
 * tuz
 * on 2023/5/24.
 */
@Data
public class BaseEntity extends BaseModel{
    /**
     * 记录id
     */
    @Id
    @Column(name = "id")
    private String id;

    /**
     * 有效标识  0：删除、无效 1：正常、有效
     *  YesNoEnum
     **/
    @Column(name = "FG_ACTIVE")
    private Integer fgActive;

    /**
     * 创建时间
     **/
    @Column(name = "INSERT_TIME")
    private Date insertTime;

    /**
     * 修改时间
     **/
    @Column(name = "UPDATE_TIME")
    private Date updateTime;

    /**
     * 删除时间
     **/
    @Column(name = "DELETE_TIME")
    private Date deleteTime;

    /**
     * 创建人
     **/
    @Column(name = "INSERT_USER")
    private String insertUser;

    /**
     * 修改人
     **/
    @Column(name = "UPDATE_USER")
    private String updateUser;

    /**
     * 删除人
     **/
    @Column(name = "DELETE_USER")
    private String deleteUser;
}
