/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;

/**
 *
 * @author lenovo
 */
@Data
@Entity
@Table(name = "ret_out_detail")
public class RetOutDetailHis implements java.io.Serializable {

    @EmbeddedId
    private RetOutCompoundKey outCompoundKey;
    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;
    @Temporal(TemporalType.DATE)
    @Column(name = "expire_date")
    private Date expireDate;
    @Column(name = "ret_out_qty")
    private Float qty;
    @Column(name = "ret_out_price")
    private Double price;
    @ManyToOne
    @JoinColumn(name = "stock_unit")
    private StockUnit stockUnit;
    @Column(name = "ret_out_amount")
    private Double amount;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "ret_out_std_weight")
    private Float stdWt;

    public RetOutDetailHis() {
    }

}
