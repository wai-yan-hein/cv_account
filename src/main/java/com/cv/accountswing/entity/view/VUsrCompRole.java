/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.entity.view;

import com.cv.accountswing.entity.UsrCompRoleKey;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="v_usr_comp_role")
public class VUsrCompRole implements java.io.Serializable{
    private UsrCompRoleKey key;
    private String companyCode;
    private String compName;
    private String roleName;
    private String userName;

    @EmbeddedId
    public UsrCompRoleKey getKey() {
        return key;
    }

    public void setKey(UsrCompRoleKey key) {
        this.key = key;
    }

    @Column(name="company_code")
    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    @Column(name="name")
    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    @Column(name="role_name")
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Column(name="user_name")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
