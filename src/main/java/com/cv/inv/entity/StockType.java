/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Lenovo
 */
@Entity
@Table(name = "stock_type")
public class StockType implements java.io.Serializable {

    @Id
    @Column(name = "stock_type_code", unique = true, nullable = false, length = 5)
    private String itemTypeCode;
    @Column(name = "stock_type_name", nullable = false, length = 50, unique = true)
    private String itemTypeName;
    @Column(name = "account_id")
    private String accountId;
    public StockType() {
    }

    public StockType(String itemTypeCode, String itemTypeName) {
        this.itemTypeCode = itemTypeCode;
        this.itemTypeName = itemTypeName;
    }
    @Override
    public String toString() {
        return this.itemTypeName;
    }

    public String getItemTypeCode() {
        return itemTypeCode;
    }

    public void setItemTypeCode(String itemTypeCode) {
        this.itemTypeCode = itemTypeCode;
    }

    public String getItemTypeName() {
        return itemTypeName;
    }

    public void setItemTypeName(String itemTypeName) {
        this.itemTypeName = itemTypeName;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    


}
