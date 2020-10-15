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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Lenovo
 */
@Entity
@Table(name = "stock_unit")
public class StockUnit implements java.io.Serializable {

    @Id
    @Column(name = "item_unit_code", unique = true, nullable = false, length = 10)
    private String itemUnitCode;
    @Column(name = "item_unit_name", nullable = false, length = 45, unique = true)
    private String itemUnitName;
    @Temporal(TemporalType.DATE)
    @Column(name = "updated_date")
    private Date updateDate;

    public StockUnit() {
    }

    @Override
    public String toString() {
        return this.itemUnitCode;
    }

    public String getItemUnitCode() {
        return itemUnitCode;
    }

    public void setItemUnitCode(String itemUnitCode) {
        this.itemUnitCode = itemUnitCode;
    }

    public String getItemUnitName() {
        return itemUnitName;
    }

    public void setItemUnitName(String itemUnitName) {
        this.itemUnitName = itemUnitName;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

}
