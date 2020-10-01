/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="trader_opening_d")
public class TraderOpeningD implements java.io.Serializable{
    private Long tranId;
    private Long tranIdH;
    private Integer traderId;
    private String currCode;
    private Double exRate;
    private Double opAmt;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "tran_id", unique = true, nullable = false)
    public Long getTranId() {
        return tranId;
    }

    public void setTranId(Long tranId) {
        this.tranId = tranId;
    }

    @Column(name="tran_id_h")
    public Long getTranIdH() {
        return tranIdH;
    }

    public void setTranIdH(Long tranIdH) {
        this.tranIdH = tranIdH;
    }

    @Column(name="trader_id")
    public Integer getTraderId() {
        return traderId;
    }

    public void setTraderId(Integer traderId) {
        this.traderId = traderId;
    }

    @Column(name="curr_code", length=15)
    public String getCurrCode() {
        return currCode;
    }

    public void setCurrCode(String currCode) {
        this.currCode = currCode;
    }

    @Column(name="ex_rate")
    public Double getExRate() {
        return exRate;
    }

    public void setExRate(Double exRate) {
        this.exRate = exRate;
    }

    @Column(name="op_amt")
    public Double getOpAmt() {
        return opAmt;
    }

    public void setOpAmt(Double opAmt) {
        this.opAmt = opAmt;
    } 
}
