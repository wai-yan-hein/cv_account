/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.entity.view;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="v_coa_tree")
public class VCoaTree implements java.io.Serializable{
    private String coaCode;
    private String coaCodeUsr;
    private String coaDesp;
    private Integer compCode;
    private Integer coaLevel;
    private String coaParent;

    @Id @Column(name="child_coa_code")
    public String getCoaCode() {
        return coaCode;
    }

    public void setCoaCode(String coaCode) {
        this.coaCode = coaCode;
    }

    @Column(name="coa_code_usr")
    public String getCoaCodeUsr() {
        return coaCodeUsr;
    }

    public void setCoaCodeUsr(String coaCodeUsr) {
        this.coaCodeUsr = coaCodeUsr;
    }

    @Column(name="child_coa_name")
    public String getCoaDesp() {
        return coaDesp;
    }

    public void setCoaDesp(String coaDesp) {
        this.coaDesp = coaDesp;
    }

    @Column(name="comp_code1")
    public Integer getCompCode() {
        return compCode;
    }

    public void setCompCode(Integer compCode) {
        this.compCode = compCode;
    }

    @Column(name="level2")
    public Integer getCoaLevel() {
        return coaLevel;
    }

    public void setCoaLevel(Integer coaLevel) {
        this.coaLevel = coaLevel;
    }

    @Column(name="coa_parent2")
    public String getCoaParent() {
        return coaParent;
    }

    public void setCoaParent(String coaParent) {
        this.coaParent = coaParent;
    }
}
