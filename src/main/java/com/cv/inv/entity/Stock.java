/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entity;

import com.cv.accountswing.entity.AppUser;
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
 * @author Lenovo
 */
@Entity
@Table(name = "stock")
public class Stock implements java.io.Serializable {

    @Id
    @Column(name = "stock_code", unique = true, nullable = false, length = 15)
    private String stockCode;
    @Column(name = "active")
    private Boolean isActive;
    @ManyToOne
    @JoinColumn(name = "brand_id")
    private StockBrand brand;
    @Column(name = "stock_name", nullable = true, length = 100, unique = true)
    private String stockName;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "stock_type_code")
    private StockType stockType;
    @ManyToOne
    @JoinColumn(name = "created_by")
    private AppUser createdBy;
    @ManyToOne
    @JoinColumn(name = "updated_by")
    private AppUser updatedBy;
    @Column(name = "barcode")
    private String barcode;
    @Column(name = "short_name", length = 50)
    private String shortName;
    @Column(name = "pur_price_mes")
    private Float purPriceMeasure;
    @Column(name = "pur_price")
    private Float purPrice;
    @ManyToOne
    @JoinColumn(name = "pur_price_unit")
    private StockUnit purPriceUnit;
    @Column(name = "sale_price_mes")
    private Float saleMeasure;
    @ManyToOne
    @JoinColumn(name = "sale_unit")
    private StockUnit saleUnit;
    @ManyToOne
    @JoinColumn(name = "pattern_id")
    private UnitPattern pattern;
    @Temporal(TemporalType.DATE)
    @Column(name = "licene_exp_date")
    private Date expireDate;
    @Column(name = "remark")
    private String remark;
    @Column(name = "sale_price_n")
    private Double salePriceN;
    @Column(name = "sale_price_a")
    private Double salePriceA;
    @Column(name = "sale_price_b")
    private Double salePriceB;
    @Column(name = "sale_price_c")
    private Double salePriceC;
    @Column(name = "sale_price_d")
    private Double salePriceD;
    @Column(name = "cost_price_std")
    private Double sttCostPrice;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public StockBrand getBrand() {
        return brand;
    }

    public void setBrand(StockBrand brand) {
        this.brand = brand;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public StockType getStockType() {
        return stockType;
    }

    public void setStockType(StockType stockType) {
        this.stockType = stockType;
    }

    public AppUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(AppUser createdBy) {
        this.createdBy = createdBy;
    }

    public AppUser getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(AppUser updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Float getPurPriceMeasure() {
        return purPriceMeasure;
    }

    public void setPurPriceMeasure(Float purPriceMeasure) {
        this.purPriceMeasure = purPriceMeasure;
    }

    public Float getPurPrice() {
        return purPrice;
    }

    public void setPurPrice(Float purPrice) {
        this.purPrice = purPrice;
    }

    public StockUnit getPurPriceUnit() {
        return purPriceUnit;
    }

    public void setPurPriceUnit(StockUnit purPriceUnit) {
        this.purPriceUnit = purPriceUnit;
    }

    public Float getSaleMeasure() {
        return saleMeasure;
    }

    public void setSaleMeasure(Float saleMeasure) {
        this.saleMeasure = saleMeasure;
    }

    public StockUnit getSaleUnit() {
        return saleUnit;
    }

    public void setSaleUnit(StockUnit saleUnit) {
        this.saleUnit = saleUnit;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Double getSalePriceN() {
        return salePriceN;
    }

    public void setSalePriceN(Double salePriceN) {
        this.salePriceN = salePriceN;
    }

    public Double getSalePriceA() {
        return salePriceA;
    }

    public void setSalePriceA(Double salePriceA) {
        this.salePriceA = salePriceA;
    }

    public Double getSalePriceB() {
        return salePriceB;
    }

    public void setSalePriceB(Double salePriceB) {
        this.salePriceB = salePriceB;
    }

    public Double getSalePriceC() {
        return salePriceC;
    }

    public void setSalePriceC(Double salePriceC) {
        this.salePriceC = salePriceC;
    }

    public Double getSalePriceD() {
        return salePriceD;
    }

    public void setSalePriceD(Double salePriceD) {
        this.salePriceD = salePriceD;
    }

    public Double getSttCostPrice() {
        return sttCostPrice;
    }

    public void setSttCostPrice(Double sttCostPrice) {
        this.sttCostPrice = sttCostPrice;
    }

    public UnitPattern getPattern() {
        return pattern;
    }

    public void setPattern(UnitPattern pattern) {
        this.pattern = pattern;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
    

}
