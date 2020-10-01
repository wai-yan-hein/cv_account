/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="privilege")
public class Privilege implements java.io.Serializable{
    private PrivilegeKey key;

    @EmbeddedId
    public PrivilegeKey getKey() {
        return key;
    }

    public void setKey(PrivilegeKey key) {
        this.key = key;
    }
}
