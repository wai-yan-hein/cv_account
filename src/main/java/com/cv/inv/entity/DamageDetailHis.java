/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author lenovo
 */
@Entity
@Table(name = "dmg_detail_his")
public class DamageDetailHis implements java.io.Serializable {

    private Long dmgDetailId;
    private String dmgVouId;
    private Stock stock;
    private Date expireDate;
    private Float qty;
    private StockUnit unit;
    private Integer uniqueId;
    private Float smallestQty;
    private Double costPrice;
    private Double amount;
    private Date updatedDate;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "dmg_detail_id", unique = true, nullable = false)
    public Long getDmgDetailId() {
        return dmgDetailId;
    }

    public void setDmgDetailId(Long dmgDetailId) {
        this.dmgDetailId = dmgDetailId;
    }

    @Column(name = "dmg_id", length = 15)
    public String getDmgVouId() {
        return dmgVouId;
    }

    public void setDmgVouId(String dmgVouId) {
        this.dmgVouId = dmgVouId;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "expire_date")
    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    @ManyToOne
    @JoinColumn(name = "stock_code")
    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    @Column(name = "dmg_qty")
    public Float getQty() {
        return qty;
    }

    public void setQty(Float qty) {
        this.qty = qty;
    }

    @Column(name = "dmg_smallest_qty")
    public Float getSmallestQty() {
        return smallestQty;
    }

    public void setSmallestQty(Float smallestQty) {
        this.smallestQty = smallestQty;
    }

    @Column(name = "unique_id")
    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    @ManyToOne
    @JoinColumn(name = "item_unit")
    public StockUnit getUnit() {
        return unit;
    }

    public void setUnit(StockUnit unit) {
        this.unit = unit;
    }

    @Column(name = "cost_price")
    public Double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
    }

    @Column(name = "amount")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

}
