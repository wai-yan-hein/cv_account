/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "chart_of_account")
public class ChartOfAccount implements java.io.Serializable {

    private String code;
    private String coaNameEng;
    private String coaNameMya;
    private Boolean active;
    private String coaTypeId;
    private Integer sortId;
    private Date createdDate;
    private Date modifiedDate;
    private String createdBy;
    private String modifiedBy;
    private String parent;
    private String option;
    private Integer compCode;
    private Integer level;
    private String coaCodeUsr;
    private String parentUsrCode;
    private String parentUsrDesp;
    private String prvCoaCode;
    private String appShortName;
    private String appTraderTypeCode;

    public ChartOfAccount() {
    }

    public ChartOfAccount(String code, String coaNameEng) {
        this.code = code;
        this.coaNameEng = coaNameEng;
    }

    @Id
    @Column(name = "coa_code", unique = true, nullable = false, length = 15)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "coa_name_eng", length = 255)
    public String getCoaNameEng() {
        return coaNameEng;
    }

    public void setCoaNameEng(String coaNameEng) {
        this.coaNameEng = coaNameEng;
    }

    @Column(name = "coa_name_mya", length = 255)
    public String getCoaNameMya() {
        return coaNameMya;
    }

    public void setCoaNameMya(String coaNameMya) {
        this.coaNameMya = coaNameMya;
    }

    @Column(name = "active")
    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Column(name = "coa_type_id", length = 15)
    public String getCoaTypeId() {
        return coaTypeId;
    }

    public void setCoaTypeId(String coaTypeId) {
        this.coaTypeId = coaTypeId;
    }

    @Column(name = "sort_order_id")
    public Integer getSortId() {
        return sortId;
    }

    public void setSortId(Integer sortId) {
        this.sortId = sortId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Column(name = "created_by", length = 15)
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "updated_by", length = 15)
    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Column(name = "coa_parent", length = 15)
    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    @Column(name = "coa_option", length = 5)
    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    @Column(name = "comp_code", length = 15)
    public Integer getCompCode() {
        return compCode;
    }

    public void setCompCode(Integer compCode) {
        this.compCode = compCode;
    }

    @Column(name = "level")
    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @Column(name = "coa_code_usr", length = 15)
    public String getCoaCodeUsr() {
        return coaCodeUsr;
    }

    public void setCoaCodeUsr(String coaCodeUsr) {
        this.coaCodeUsr = coaCodeUsr;
    }

    @Column(name = "parent_usr_code", length = 15)
    public String getParentUsrCode() {
        return parentUsrCode;
    }

    public void setParentUsrCode(String parentUsrCode) {
        this.parentUsrCode = parentUsrCode;
    }

    @Column(name = "parent_usr_desp", length = 255)
    public String getParentUsrDesp() {
        return parentUsrDesp;
    }

    public void setParentUsrDesp(String parentUsrDesp) {
        this.parentUsrDesp = parentUsrDesp;
    }

    @Column(name = "prv_coa_code", length = 15)
    public String getPrvCoaCode() {
        return prvCoaCode;
    }

    public void setPrvCoaCode(String prvCoaCode) {
        this.prvCoaCode = prvCoaCode;
    }

    @Column(name = "app_short_name", length = 10)
    public String getAppShortName() {
        return appShortName;
    }

    public void setAppShortName(String appShortName) {
        this.appShortName = appShortName;
    }

    @Column(name = "app_trader_type_code", length = 15)
    public String getAppTraderTypeCode() {
        return appTraderTypeCode;
    }

    public void setAppTraderTypeCode(String appTraderTypeCode) {
        this.appTraderTypeCode = appTraderTypeCode;
    }

    @Override
    public String toString() {
        return coaNameEng;
    }

}
