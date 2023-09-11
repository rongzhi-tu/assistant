package com.bsoft.assistant.model.vo.message;

import lombok.Data;

import javax.security.auth.Subject;
import java.io.Serializable;
import java.security.Principal;
/**
 * Description:
 * Author: 杰明Jamin
 * Date: 2017/11/5 8:39
 */
@Data
public class Authentication implements Principal,Serializable {

    private String name;

    public Authentication(String name) {
        this.name = name;
    }
    public Authentication() {
    }

    @Override
    public boolean implies(Subject subject) {
        return false;
    }
}
