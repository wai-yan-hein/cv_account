/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="assign_dept")
public class AssignDept implements java.io.Serializable {
    private AssignDeptKey key;
    private boolean allow;
    
    @EmbeddedId
    public AssignDeptKey getKey() {
        return key;
    }

    public void setKey(AssignDeptKey key) {
        this.key = key;
    }

    @Column(name="allow")
    public boolean isAllow() {
        return allow;
    }

    public void setAllow(boolean allow) {
        this.allow = allow;
    }
}
