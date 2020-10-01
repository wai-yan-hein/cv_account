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
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
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

    @Column(name = "pur_mes")
    private Float purMeasure;

    @ManyToOne
    @JoinColumn(name = "pur_unit")
    private StockUnit purUnit;

    @Column(name = "sale_price_mes")
    private Float saleMeasure;
    @ManyToOne
    @JoinColumn(name = "sale_unit")
    private StockUnit saleUnit;

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

}
