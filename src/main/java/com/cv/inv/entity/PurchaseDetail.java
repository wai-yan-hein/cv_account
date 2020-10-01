/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entity;

import com.cv.accountswing.entity.Department;
import java.io.Serializable;
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
@Table(name = "pur_detail")
public class PurchaseDetail implements Serializable {

    @Id
    @Column(name = "pur_detail_code", unique = true, nullable = false)
    private String purDetailCode;
    @ManyToOne
    @JoinColumn(name = "stock_code", nullable = false)
    private Stock stock;
    @Temporal(TemporalType.DATE)
    @Column(name = "exp_date")
    private Date expDate;
    @Column(name = "qty", nullable = false)
    private Float qty;
    @Column(name = "std_wt", nullable = false)
    private Float stdWeight;
    @ManyToOne
    @JoinColumn(name = "item_unit_code", nullable = false)
    private StockUnit purUnit;
    @Column(name = "avg_wt")
    private Float avgWeight;
    @Column(name = "pur_price", nullable = false)
    private Float purPrice;
    @Column(name = "pur_amt", nullable = false)
    private Float purAmt;
    @ManyToOne
    @JoinColumn(name = "loc_id")
    private Location location;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "gl_id")
    private Long glId;
    @Column(name = "small_wt", nullable = false)
    private Float smallestWT;
    @Column(name = "small_unit", nullable = false)
    private String smallestUnit;
}
