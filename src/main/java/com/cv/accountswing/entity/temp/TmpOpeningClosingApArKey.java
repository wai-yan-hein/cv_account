/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.entity.temp;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author winswe
 */
@Embeddable
public class TmpOpeningClosingApArKey implements Serializable{
    private String coaId;
    private String currId;
    private String userId;
    private Integer cvId;
    private String deptId;
    
    public TmpOpeningClosingApArKey() {}
    
    @Column(name="coa_id", nullable=false, length=25)
    public String getCoaId() {
        return coaId;
    }

    public void setCoaId(String coaId) {
        this.coaId = coaId;
    }

    @Column(name="curr_id", nullable=false, length=15)
    public String getCurrId() {
        return currId;
    }

    public void setCurrId(String currId) {
        this.currId = currId;
    }

    @Column(name="user_id", nullable=false, length=15)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name="cv_id")
    public Integer getCvId() {
        return cvId;
    }

    public void setCvId(Integer cvId) {
        this.cvId = cvId;
    }

    @Column(name="dept_id")
    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.coaId);
        hash = 23 * hash + Objects.hashCode(this.currId);
        hash = 23 * hash + Objects.hashCode(this.userId);
        hash = 23 * hash + Objects.hashCode(this.cvId);
        hash = 23 * hash + Objects.hashCode(this.deptId);
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
        final TmpOpeningClosingApArKey other = (TmpOpeningClosingApArKey) obj;
        if (!Objects.equals(this.coaId, other.coaId)) {
            return false;
        }
        if (!Objects.equals(this.currId, other.currId)) {
            return false;
        }
        if (!Objects.equals(this.userId, other.userId)) {
            return false;
        }
        if (!Objects.equals(this.cvId, other.cvId)) {
            return false;
        }
        if (!Objects.equals(this.deptId, other.deptId)) {
            return false;
        }
        return true;
    }
}
