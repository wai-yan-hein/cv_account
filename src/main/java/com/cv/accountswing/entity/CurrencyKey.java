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
public class CurrencyKey implements Serializable {

    private String code;
    private Integer compCode;

    public CurrencyKey(String code, Integer compCode) {
        this.code = code;
        this.compCode = compCode;
    }

    public CurrencyKey() {
    }
    

    @Column(name = "cur_code", unique = true, nullable = false, length = 15)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "comp_code")
    public Integer getCompCode() {
        return compCode;
    }

    public void setCompCode(Integer compCode) {
        this.compCode = compCode;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.code);
        hash = 37 * hash + Objects.hashCode(this.compCode);
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
        final CurrencyKey other = (CurrencyKey) obj;
        if (!Objects.equals(this.code, other.code)) {
            return false;
        }
        if (!Objects.equals(this.compCode, other.compCode)) {
            return false;
        }
        return true;
    }

}
