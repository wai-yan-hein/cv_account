/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entity;

import java.util.Date;
import javax.persistence.*;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@Entity
@Table(name = "ret_in_detail")
public class RetInDetailHis implements java.io.Serializable {

    @EmbeddedId
    private RetInCompoundKey inCompoundKey;
    @ManyToOne
    @JoinColumn(name = "stock_id")
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
    private Float stdWt;;
}
