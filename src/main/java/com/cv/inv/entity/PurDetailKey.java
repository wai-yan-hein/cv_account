/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Lenovo
 */
@Embeddable
public class PurDetailKey implements Serializable {

    @Column(name = "vou_id", length = 15)
    private String vouId;
    @Column(name = "pur_detail_code", length = 20)
    private String purDetailId;

    public String getVouId() {
        return vouId;
    }

    public void setVouId(String vouId) {
        this.vouId = vouId;
    }

    public String getPurDetailId() {
        return purDetailId;
    }

    public void setPurDetailId(String purDetailId) {
        this.purDetailId = purDetailId;
    }

    public PurDetailKey(String vouId, String purDetailId) {
        this.vouId = vouId;
        this.purDetailId = purDetailId;
    }

}
