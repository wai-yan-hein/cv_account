/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author winswe
 */
@Embeddable
public class CoaDeptMapKey implements Serializable{
    
    private Integer compCode;
    private String coaCode;
    private String deptCode;

    @Column(name="comp_id", unique=true, nullable=false)
    public Integer getCompCode() {
        return compCode;
    }

    public void setCompCode(Integer compCode) {
        this.compCode = compCode;
    }

    @Column(name="coa_code", unique=true, nullable=false, length=15)
    public String getCoaCode() {
        return coaCode;
    }

    public void setCoaCode(String coaCode) {
        this.coaCode = coaCode;
    }

    @Column(name="dept_code", unique=true, nullable=false, length=15)
    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.compCode);
        hash = 89 * hash + Objects.hashCode(this.coaCode);
        hash = 89 * hash + Objects.hashCode(this.deptCode);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CoaDeptMapKey other = (CoaDeptMapKey) obj;
        if (!Objects.equals(this.coaCode, other.coaCode)) {
            return false;
        }
        if (!Objects.equals(this.deptCode, other.deptCode)) {
            return false;
        }
        if (!Objects.equals(this.compCode, other.compCode)) {
            return false;
        }
        return true;
    }
}
