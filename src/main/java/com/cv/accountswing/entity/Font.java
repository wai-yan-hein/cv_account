/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="font")
public class Font implements java.io.Serializable{
    private FontKey key;
    private Integer integraKeyCode;

    public Font(){
        key = new FontKey();
    }
    
    @EmbeddedId
    public FontKey getKey() {
        return key;
    }

    public void setKey(FontKey key) {
        this.key = key;
    }

    @Column(name="integrakeycode")
    public Integer getIntegraKeyCode() {
        return integraKeyCode;
    }

    public void setIntegraKeyCode(Integer integraKeyCode) {
        this.integraKeyCode = integraKeyCode;
    }
}
