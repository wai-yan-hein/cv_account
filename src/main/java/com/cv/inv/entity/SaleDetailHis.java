/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Entity
@Table(name = "sale_detail_his")
public class SaleDetailHis implements java.io.Serializable {

    private Long saleDetailId;
    private Stock stockCode;
    private Date expDate;
    private Float quantity;
    private Float saleSmallestQty;
    private StockUnit itemUnit;
    private Double price;
    private Double discount;
    private String discType;
    private Double amount;
    private Location location;
    private Integer uniqueId;
    private Float stdWeight;

    public SaleDetailHis() {

    }

    @Id
    @Column(name = "sale_detail_id", unique = true, nullable = false)
    public Long getSaleDetailId() {
        return saleDetailId;
    }

    public void setSaleDetailId(Long saleDetailId) {
        this.saleDetailId = saleDetailId;
    }

    @ManyToOne
    @JoinColumn(name = "stock_id")
    public Stock getStockCode() {
        return stockCode;
    }

    public void setStockCode(Stock stockCode) {
        this.stockCode = stockCode;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "expire_date")
    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    @Column(name = "sale_qty")
    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    @Column(name = "sale_smallest_qty")
    public Float getSaleSmallestQty() {
        return saleSmallestQty;
    }

    public void setSaleSmallestQty(Float saleSmallestQty) {
        this.saleSmallestQty = saleSmallestQty;
    }

    @ManyToOne
    @JoinColumn(name = "item_unit")
    public StockUnit getItemUnit() {
        return itemUnit;
    }

    public void setItemUnit(StockUnit itemUnit) {
        this.itemUnit = itemUnit;
    }

    @Column(name = "sale_price")
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Column(name = "item_discount")
    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    @Column(name = "disc_type")
    public String getDiscType() {
        return discType;
    }

    public void setDiscType(String discType) {
        this.discType = discType;
    }

    @Column(name = "sale_amount")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Column(name = "location_id")
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Column(name = "unique_id")
    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Column(name = "std_weight")
    public Float getStdWeight() {
        return stdWeight;
    }

    public void setStdWeight(Float stdWeight) {
        this.stdWeight = stdWeight;
    }

}
