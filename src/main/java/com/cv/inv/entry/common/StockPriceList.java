/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

/**
 *
 * @author WSwe
 */
public class StockPriceList {
    private String type;
    private Double price;
    
    public StockPriceList(String type, Double price){
        this.type = type;
        this.price = price;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
