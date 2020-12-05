/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entity;

import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author Lenovo
 */
@Entity
@Table(name = "ret_in_his_detail")
public class RetInHisDetail implements java.io.Serializable {

    @EmbeddedId
    private RetInCompoundKey inCompoundKey;
    @ManyToOne
    @JoinColumn(name = "stock_code")
    private Stock stock;
    @Temporal(TemporalType.DATE)
    @Column(name = "expire_date")
    private Date expireDate;
    @Column(name = "ret_in_qty")
    private Float qty;
    @Column(name = "ret_in_price")
    private Double price;
    @ManyToOne
    @JoinColumn(name = "stock_unit")
    private StockUnit stockUnit;
    @Column(name = "ret_in_amount")
    private Double amount;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "ret_in_std_weight")
    private Float stdWt;
    @Column(name = "small_wt")
    private Float smallWeight;
    @ManyToOne
    @JoinColumn(name = "small_unit")
    private StockUnit smallUnit;

    public RetInCompoundKey getInCompoundKey() {
        return inCompoundKey;
    }

    public void setInCompoundKey(RetInCompoundKey inCompoundKey) {
        this.inCompoundKey = inCompoundKey;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public Float getQty() {
        return qty;
    }

    public void setQty(Float qty) {
        this.qty = qty;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public StockUnit getStockUnit() {
        return stockUnit;
    }

    public void setStockUnit(StockUnit stockUnit) {
        this.stockUnit = stockUnit;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Float getStdWt() {
        return stdWt;
    }

    public void setStdWt(Float stdWt) {
        this.stdWt = stdWt;
    }

    public Float getSmallWeight() {
        return smallWeight;
    }

    public void setSmallWeight(Float smallWeight) {
        this.smallWeight = smallWeight;
    }

    public StockUnit getSmallUnit() {
        return smallUnit;
    }

    public void setSmallUnit(StockUnit smallUnit) {
        this.smallUnit = smallUnit;
    }

}
