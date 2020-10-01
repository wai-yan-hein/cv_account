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
public class ABankKey implements Serializable{
    private String bankCode;
    private Integer compId;

    @Column(name="bank_code", length=15)
    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    @Column(name="comp_id")
    public Integer getCompId() {
        return compId;
    }

    public void setCompId(Integer compId) {
        this.compId = compId;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.bankCode);
        hash = 37 * hash + Objects.hashCode(this.compId);
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
        final ABankKey other = (ABankKey) obj;
        if (!Objects.equals(this.bankCode, other.bankCode)) {
            return false;
        }
        if (!Objects.equals(this.compId, other.compId)) {
            return false;
        }
        return true;
    }
}
