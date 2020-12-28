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
    private Float price;
    
    public StockPriceList(String type, Float price){
        this.type = type;
        this.price = price;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
