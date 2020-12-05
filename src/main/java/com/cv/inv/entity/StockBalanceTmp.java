/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Lenovo
 */
@Entity
@Table(name = "tmp_stock_balance")
public class StockBalanceTmp implements Serializable {

    @EmbeddedId
    private StockBalanceTmpKey key;
    @Column(name = "change_wt")
    private Float changeWt;
    @Column(name = "change_unit")
    private String changeUnit;
    @Column(name = "change_wt_2")
    private Float changeWt2;
    @Column(name = "change_unit_2")
    private String changeUnit2;
    @Column(name = "qty", nullable = false)
    private Float qty;
    @Column(name = "std_wt", nullable = false)
    private Float stdWt;
    @Column(name = "std_wt_ttl", nullable = false)
    private Float stdWtTotal;
    @Column(name = "small_unit", nullable = false)
    private String unit;

    public Float getQty() {
        return qty;
    }

    public void setQty(Float qty) {
        this.qty = qty;
    }

    public Float getStdWt() {
        return stdWt;
    }

    public void setStdWt(Float stdWt) {
        this.stdWt = stdWt;
    }

    public Float getStdWtTotal() {
        return stdWtTotal;
    }

    public void setStdWtTotal(Float stdWtTotal) {
        this.stdWtTotal = stdWtTotal;
    }

    public String getUnit() {
        return unit;
    }

    public StockBalanceTmpKey getKey() {
        return key;
    }

    public void setKey(StockBalanceTmpKey key) {
        this.key = key;
    }

    public Float getChangeWt() {
        return changeWt;
    }

    public void setChangeWt(Float changeWt) {
        this.changeWt = changeWt;
    }

    public String getChangeUnit() {
        return changeUnit;
    }

    public void setChangeUnit(String changeUnit) {
        this.changeUnit = changeUnit;
    }

    public Float getChangeWt2() {
        return changeWt2;
    }

    public void setChangeWt2(Float changeWt2) {
        this.changeWt2 = changeWt2;
    }

    public String getChangeUnit2() {
        return changeUnit2;
    }

    public void setChangeUnit2(String changeUnit2) {
        this.changeUnit2 = changeUnit2;
    }
    
    

}
