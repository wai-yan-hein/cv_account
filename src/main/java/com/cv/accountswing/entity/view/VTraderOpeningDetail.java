/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.entity.view;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="v_trader_op_detail")
public class VTraderOpeningDetail implements java.io.Serializable{
    private Long tranId;
    private Long tranIdH;
    private Long traderId;
    private String currCode;
    private Double exRate;
    private Double opAmt;
    private String traderType;
    private String traderCode;
    private String traderName;
    private Boolean active;
    private String compCode;
    private String appShortName;
    private String appTraderCode;
    private String currName;
    private String currSymbol;

    @Id
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
    public Long getTraderId() {
        return traderId;
    }

    public void setTraderId(Long traderId) {
        this.traderId = traderId;
    }

    @Column(name="curr_code")
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

    @Column(name="discriminator")
    public String getTraderType() {
        return traderType;
    }

    public void setTraderType(String traderType) {
        this.traderType = traderType;
    }

    @Column(name="trader_code")
    public String getTraderCode() {
        return traderCode;
    }

    public void setTraderCode(String traderCode) {
        this.traderCode = traderCode;
    }

    @Column(name="trader_name")
    public String getTraderName() {
        return traderName;
    }

    public void setTraderName(String traderName) {
        this.traderName = traderName;
    }

    @Column(name="active")
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Column(name="comp_code")
    public String getCompCode() {
        return compCode;
    }

    public void setCompCode(String compCode) {
        this.compCode = compCode;
    }

    @Column(name="app_short_name")
    public String getAppShortName() {
        return appShortName;
    }

    public void setAppShortName(String appShortName) {
        this.appShortName = appShortName;
    }

    @Column(name="app_trader_code")
    public String getAppTraderCode() {
        return appTraderCode;
    }

    public void setAppTraderCode(String appTraderCode) {
        this.appTraderCode = appTraderCode;
    }

    @Column(name="cur_name")
    public String getCurrName() {
        return currName;
    }

    public void setCurrName(String currName) {
        this.currName = currName;
    }

    @Column(name="cur_symbol")
    public String getCurrSymbol() {
        return currSymbol;
    }

    public void setCurrSymbol(String currSymbol) {
        this.currSymbol = currSymbol;
    }
}
