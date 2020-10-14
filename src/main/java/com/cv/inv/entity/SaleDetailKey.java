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
 * @author Mg Kyaw Thura Aung
 */
@Embeddable
public class SaleDetailKey implements Serializable {

    private String vouId;
    private String saleDetailId;

    public SaleDetailKey(String vouId, String saleDetailId) {
        this.vouId = vouId;
        this.saleDetailId = saleDetailId;
    }

    @Column(name = "vou_id", length = 15)
    public String getVouId() {
        return vouId;
    }

    public void setVouId(String vouId) {
        this.vouId = vouId;
    }

    @Column(name = "sale_detail_id", length = 20)
    public String getSaleDetailId() {
        return saleDetailId;
    }

    public void setSaleDetailId(String saleDetailId) {
        this.saleDetailId = saleDetailId;
    }

}
