/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.entity;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

/**
 *
 * @author winswe
 */
@Entity
@FilterDef(name="compFilter", parameters=@ParamDef(name="companyParam", type="string" ))
@Table(name="chart_of_account")
public class COALevel implements java.io.Serializable{
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
    private COALevel parent;
    private String option;
    private Integer compCode;
    private List<ChartOfAccount> listChild;
    private Integer level;
    
    @Id
    @Column(name="coa_code", unique=true, nullable=false, length=15)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    @Column(name="active")
    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Column(name="coa_type_id", length=15)
    public String getCoaTypeId() {
        return coaTypeId;
    }

    public void setCoaTypeId(String coaTypeId) {
        this.coaTypeId = coaTypeId;
    }

    @Column(name="sort_order_id")
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

    @Column(name="created_by", length=15)
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name="updated_by", length=15)
    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @ManyToOne(cascade={CascadeType.PERSIST})
    @JoinColumn(name="coa_parent")
    public COALevel getParent() {
        return parent;
    }

    public void setParent(COALevel parent) {
        if(parent != null){
            this.parent = parent;
            this.level = 1;

            while(parent.parent != null){
                this.level++;
                parent = parent.parent;
            }
        }
    }

    @Column(name="coa_option", length=5)
    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    @Column(name="comp_code", length=15)
    public Integer getCompCode() {
        return compCode;
    }

    public void setCompCode(Integer compCode) {
        this.compCode = compCode;
    }

    @OneToMany(fetch=FetchType.EAGER)
    @JoinColumn(name="coa_parent")
    @Filter(name="compFilter", condition="comp_code = :companyParam")
    @OrderBy("coaNameEng ASC")
    public List<ChartOfAccount> getListChild() {
        return listChild;
    }

    public void setListChild(List<ChartOfAccount> listChild) {
        this.listChild = listChild;
    }

    @Transient
    public Integer getLevel() {
        return level;
    }
}
