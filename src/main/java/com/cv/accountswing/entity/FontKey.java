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
public class FontKey implements Serializable{
    private Integer winKeyCode;
    private Integer zawgyiKeyCode;

    @Column(name="winkeycode")
    public Integer getWinKeyCode() {
        return winKeyCode;
    }

    public void setWinKeyCode(Integer winKeyCode) {
        this.winKeyCode = winKeyCode;
    }

    @Column(name="zawgyikeycode")
    public Integer getZawgyiKeyCode() {
        return zawgyiKeyCode;
    }

    public void setZawgyiKeyCode(Integer zawgyiKeyCode) {
        this.zawgyiKeyCode = zawgyiKeyCode;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.winKeyCode);
        hash = 37 * hash + Objects.hashCode(this.zawgyiKeyCode);
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
        final FontKey other = (FontKey) obj;
        if (!Objects.equals(this.winKeyCode, other.winKeyCode)) {
            return false;
        }
        if (!Objects.equals(this.zawgyiKeyCode, other.zawgyiKeyCode)) {
            return false;
        }
        return true;
    }
}
