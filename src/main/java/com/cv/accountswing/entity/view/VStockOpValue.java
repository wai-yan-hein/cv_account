/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.entity.view;

import com.cv.accountswing.entity.StockOpValueKey;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="v_stock_op_value")
public class VStockOpValue implements java.io.Serializable{
    private StockOpValueKey key;
    private Double amount;
    private String remark;
    private String status;
    private String createdBy;
    private Date createdDate;
    private String updatedBy;
    private Date updatedDate;
    private String coaCodeUsr;
    private String coaNameEng;
    private String deptCodeUsr;
    private String deptName;
    private String currName;
    
    public VStockOpValue(){
        key = new StockOpValueKey();
    }
    
    @EmbeddedId
    public StockOpValueKey getKey() {
        return key;
    }

    public void setKey(StockOpValueKey key) {
        this.key = key;
    }

    @Column(name="amount")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Column(name="remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Transient
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name="created_by")
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_date")
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Column(name="updated_by")
    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="updated_date")
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Column(name="coa_code_usr")
    public String getCoaCodeUsr() {
        return coaCodeUsr;
    }

    public void setCoaCodeUsr(String coaCodeUsr) {
        this.coaCodeUsr = coaCodeUsr;
    }

    @Column(name="coa_name_eng")
    public String getCoaNameEng() {
        return coaNameEng;
    }

    public void setCoaNameEng(String coaNameEng) {
        this.coaNameEng = coaNameEng;
    }

    @Column(name="usr_code")
    public String getDeptCodeUsr() {
        return deptCodeUsr;
    }

    public void setDeptCodeUsr(String deptCodeUsr) {
        this.deptCodeUsr = deptCodeUsr;
    }

    @Column(name="dept_name")
    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    @Column(name="cur_name")
    public String getCurrName() {
        return currName;
    }

    public void setCurrName(String currName) {
        this.currName = currName;
    }
}
