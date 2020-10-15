/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entity.view;

import com.cv.inv.entity.RetOutCompoundKey;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author lenovo
 */
@Entity
@Table(name = "v_ret_out")
public class VRetOut implements Serializable {
    @EmbeddedId
    private RetOutCompoundKey key;
    @Column(name = "stock_id")
    private String stockId;
    @Column(name = "cv_id")
    private Long traderId;
    @Column(name = "trader_name")
    private String traderName;
    @Temporal(TemporalType.DATE)
    @Column(name = "gl_date")
    private Date glDate;
    @Column(name = "vou_total")
    private Double vouTotal;
    @Column(name = "paid")
    private Double vouPaid;
    @Column(name = "balance")
    private Double vouBalance;
    @Column(name = "location_id")
    private Integer locationId;
    @Column(name = "comp_id")
    private Integer compId;
    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "user_short_name")
    private String  userName;
    @Column(name = "from_cur_id")
    private String  fromCurId;

    public RetOutCompoundKey getKey() {
        return key;
    }

    public void setKey(RetOutCompoundKey key) {
        this.key = key;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public Long getTraderId() {
        return traderId;
    }

    public void setTraderId(Long traderId) {
        this.traderId = traderId;
    }

    public String getTraderName() {
        return traderName;
    }

    public void setTraderName(String traderName) {
        this.traderName = traderName;
    }

    public Date getGlDate() {
        return glDate;
    }

    public void setGlDate(Date glDate) {
        this.glDate = glDate;
    }

    public Double getVouTotal() {
        return vouTotal;
    }

    public void setVouTotal(Double vouTotal) {
        this.vouTotal = vouTotal;
    }

    public Double getVouPaid() {
        return vouPaid;
    }

    public void setVouPaid(Double vouPaid) {
        this.vouPaid = vouPaid;
    }

    public Double getVouBalance() {
        return vouBalance;
    }

    public void setVouBalance(Double vouBalance) {
        this.vouBalance = vouBalance;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getCompId() {
        return compId;
    }

    public void setCompId(Integer compId) {
        this.compId = compId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFromCurId() {
        return fromCurId;
    }

    public void setFromCurId(String fromCurId) {
        this.fromCurId = fromCurId;
    }
    
}
