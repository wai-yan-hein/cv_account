/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author winswe
 */
@Embeddable
public class StockOpValueKey implements Serializable{
    private Date tranDate;
    private String coaCode;
    private String deptCode;
    private String currency;
    private Integer compId;
    
    @Temporal(TemporalType.DATE)
    @Column(name="tran_date", nullable=false)
    public Date getTranDate() {
        return tranDate;
    }

    public void setTranDate(Date tranDate) {
        this.tranDate = tranDate;
    }

    @Column(name="coa_code", nullable=false, length=15)
    public String getCoaCode() {
        return coaCode;
    }

    public void setCoaCode(String coaCode) {
        this.coaCode = coaCode;
    }

    @Column(name="dept_code", nullable=false, length=15)
    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    @Column(name="curr_code", nullable=false, length=15)
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Column(name="comp_id", nullable=false)
    public Integer getCompId() {
        return compId;
    }

    public void setCompId(Integer compId) {
        this.compId = compId;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.tranDate);
        hash = 53 * hash + Objects.hashCode(this.coaCode);
        hash = 53 * hash + Objects.hashCode(this.deptCode);
        hash = 53 * hash + Objects.hashCode(this.currency);
        hash = 53 * hash + Objects.hashCode(this.compId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StockOpValueKey other = (StockOpValueKey) obj;
        if (!Objects.equals(this.tranDate, other.tranDate)) {
            return false;
        }
        if (!Objects.equals(this.coaCode, other.coaCode)) {
            return false;
        }
        if (!Objects.equals(this.deptCode, other.deptCode)) {
            return false;
        }
        if (!Objects.equals(this.currency, other.currency)) {
            return false;
        }
        if (!Objects.equals(this.compId, other.compId)) {
            return false;
        }
        return true;
    }
}
