/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entity;

import java.io.Serializable;
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
 * @author Lenovo
 */
@Entity
@Table(name = "stock_in_out_detail")
public class StockInOutDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;
    @ManyToOne
    @JoinColumn(name = "loc_id")
    private Location location;
    @Column(name = "in_qty")
    private Float inQty;
    @Column(name = "in_weight")
    private Float inWeight;
    @ManyToOne
    @JoinColumn(name = "in_unit")
    private StockUnit inUnit;
    @Column(name = "out_qty")
    private Float outQty;
    @Column(name = "out_weight")
    private Float outWeight;
    @ManyToOne
    @JoinColumn(name = "out_unit")
    private StockUnit outUnit;
    @Column(name = "desp")
    private String description;
    @Column(name = "remark")
    private String remark;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    private Date date;
    @Column(name = "small_in_weight")
    private Float smallInWeight;
    @ManyToOne
    @JoinColumn(name = "small_in_unit")
    private StockUnit smallInUnit;
    @Column(name = "small_out_weight")
    private Float samllOutWeight;
    @ManyToOne
    @JoinColumn(name = "small_out_unit")
    private StockUnit smallOutUnit;
    @Column(name = "batch_code")
    private String batchCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Float getInQty() {
        return inQty;
    }

    public void setInQty(Float inQty) {
        this.inQty = inQty;
    }

    public Float getInWeight() {
        return inWeight;
    }

    public void setInWeight(Float inWeight) {
        this.inWeight = inWeight;
    }

    public StockUnit getInUnit() {
        return inUnit;
    }

    public void setInUnit(StockUnit inUnit) {
        this.inUnit = inUnit;
    }

    public Float getOutQty() {
        return outQty;
    }

    public void setOutQty(Float outQty) {
        this.outQty = outQty;
    }

    public Float getOutWeight() {
        return outWeight;
    }

    public void setOutWeight(Float outWeight) {
        this.outWeight = outWeight;
    }

    public StockUnit getOutUnit() {
        return outUnit;
    }

    public void setOutUnit(StockUnit outUnit) {
        this.outUnit = outUnit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Float getSmallInWeight() {
        return smallInWeight;
    }

    public void setSmallInWeight(Float smallInWeight) {
        this.smallInWeight = smallInWeight;
    }

    public StockUnit getSmallInUnit() {
        return smallInUnit;
    }

    public void setSmallInUnit(StockUnit smallInUnit) {
        this.smallInUnit = smallInUnit;
    }

    public Float getSamllOutWeight() {
        return samllOutWeight;
    }

    public void setSamllOutWeight(Float samllOutWeight) {
        this.samllOutWeight = samllOutWeight;
    }

    public StockUnit getSmallOutUnit() {
        return smallOutUnit;
    }

    public void setSmallOutUnit(StockUnit smallOutUnit) {
        this.smallOutUnit = smallOutUnit;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

}
