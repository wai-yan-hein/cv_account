/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.entity.view;

import com.cv.accountswing.entity.AssignDeptKey;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="v_assign_dept")
public class VAssignDept implements java.io.Serializable {
    private AssignDeptKey key;
    private boolean allow;
    private String compCode;
    private String compName;
    private String deptUsrCode;
    private String deptName;
    
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

    @Column(name="comp_code")
    public String getCompCode() {
        return compCode;
    }

    public void setCompCode(String compCode) {
        this.compCode = compCode;
    }

    @Column(name="comp_name")
    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    @Column(name="dept_usr_code")
    public String getDeptUsrCode() {
        return deptUsrCode;
    }

    public void setDeptUsrCode(String deptUsrCode) {
        this.deptUsrCode = deptUsrCode;
    }

    @Column(name="dept_name")
    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
}
