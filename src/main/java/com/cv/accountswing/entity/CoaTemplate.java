/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="coa_template")
public class CoaTemplate implements java.io.Serializable{
    private Long tranId;
    private Integer businessType;
    private String coaCode;
    private String coaNameEng;
    private String coaNameMya;
    private Integer sortOrderId;
    private String coaParent;
    private String coaOption;
    private Integer level;
    private String coaCodeUser;
    private String parentUserCode;
    private String parentUserDesp;
    
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "tran_id", unique = true, nullable = false)
    public Long getTranId() {
        return tranId;
    }

    public void setTranId(Long tranId) {
        this.tranId = tranId;
    }

    @Column(name="business_type")
    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    @Column(name="coa_code")
    public String getCoaCode() {
        return coaCode;
    }

    public void setCoaCode(String coaCode) {
        this.coaCode = coaCode;
    }

    @Column(name="coa_name_eng", length=255)
    public String getCoaNameEng() {
        return coaNameEng;
    }

    public void setCoaNameEng(String coaNameEng) {
        this.coaNameEng = coaNameEng;
    }

    @Column(name="coa_name_mya", length=255)
    public String getCoaNameMya() {
        return coaNameMya;
    }

    public void setCoaNameMya(String coaNameMya) {
        this.coaNameMya = coaNameMya;
    }

    @Column(name="sort_order_id")
    public Integer getSortOrderId() {
        return sortOrderId;
    }

    public void setSortOrderId(Integer sortOrderId) {
        this.sortOrderId = sortOrderId;
    }

    @Column(name="coa_parent", length=15)
    public String getCoaParent() {
        return coaParent;
    }

    public void setCoaParent(String coaParent) {
        this.coaParent = coaParent;
    }

    @Column(name="coa_option", length=5)
    public String getCoaOption() {
        return coaOption;
    }

    public void setCoaOption(String coaOption) {
        this.coaOption = coaOption;
    }

    @Column(name="level")
    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @Column(name="coa_code_usr", length=15)
    public String getCoaCodeUser() {
        return coaCodeUser;
    }

    public void setCoaCodeUser(String coaCodeUser) {
        this.coaCodeUser = coaCodeUser;
    }

    @Column(name="parent_usr_code", length=15)
    public String getParentUserCode() {
        return parentUserCode;
    }

    public void setParentUserCode(String parentUserCode) {
        this.parentUserCode = parentUserCode;
    }

    @Column(name="parent_usr_desp", length=255)
    public String getParentUserDesp() {
        return parentUserDesp;
    }

    public void setParentUserDesp(String parentUserDesp) {
        this.parentUserDesp = parentUserDesp;
    }
}
