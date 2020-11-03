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
public class TmpOpeningClosingKey implements Serializable{
    private String coaId;
    private String currId;
    private String userId;

    public TmpOpeningClosingKey() {}
    
    @Column(name="coa_code", nullable=false, length=25)
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.coaId);
        hash = 67 * hash + Objects.hashCode(this.currId);
        hash = 67 * hash + Objects.hashCode(this.userId);
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
        final TmpOpeningClosingKey other = (TmpOpeningClosingKey) obj;
        if (!Objects.equals(this.coaId, other.coaId)) {
            return false;
        }
        if (!Objects.equals(this.currId, other.currId)) {
            return false;
        }
        if (!Objects.equals(this.userId, other.userId)) {
            return false;
        }
        return true;
    }
}
