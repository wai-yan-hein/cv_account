/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@Entity
@Table(name = "stock_brand")
public class StockBrand implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "brand_id", unique = true, nullable = false)
    private Integer brandId;
    @Column(name = "brand_name", nullable = false, length = 80, unique = true)
    private String brandName;
    @Column(name = "mig_id")
    private Integer migId;

    @Override
    public String toString() {
        return brandName;
    }

}
