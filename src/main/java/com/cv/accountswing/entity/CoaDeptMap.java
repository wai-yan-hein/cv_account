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
@Table(name="coa_dept_map")
public class CoaDeptMap implements java.io.Serializable {
    private CoaDeptMapKey key;

    @EmbeddedId
    public CoaDeptMapKey getKey() {
        return key;
    }

    public void setKey(CoaDeptMapKey key) {
        this.key = key;
    }
}
