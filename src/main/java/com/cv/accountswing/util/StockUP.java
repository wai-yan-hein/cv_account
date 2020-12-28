/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.util;

import com.cv.inv.entity.Stock;
import com.cv.inv.entry.common.StockPriceList;
import java.util.ArrayList;
import java.util.List;
import com.cv.accountswing.common.Global;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Component
public class StockUP {

    static Logger log = Logger.getLogger(StockUP.class.getName());

    public StockUP() {
    }

    public void add(Stock stock) {
        if (!Global.hasUnit.containsKey(stock.getStockCode())) {
            String stockCode = stock.getStockCode();

            List<StockPriceList> listPrice = new ArrayList();
            listPrice.add(new StockPriceList("N", stock.getSalePriceN()));
            listPrice.add(new StockPriceList("A", stock.getSalePriceA()));
            listPrice.add(new StockPriceList("B", stock.getSalePriceB()));
            listPrice.add(new StockPriceList("C", stock.getSalePriceC()));
            listPrice.add(new StockPriceList("D", stock.getSalePriceD()));

            Global.hasPrice.put(stockCode, listPrice);
        }
    }

    public List<StockPriceList> getPriceList(String stockCode) {
        if (Global.hasPrice.containsKey(stockCode)) {
            return Global.hasPrice.get(stockCode);
        } else {
            return null;
        }
    }

    public Float getPrice(String stockCode, String priceType) {
        Float price = 0.0f;
        List<StockPriceList> priceList = getPriceList(stockCode);
        int index = -1;

        switch (priceType) {
            case "N":
                index = 0;
                break;
            case "A":
                index = 1;
                break;
            case "B":
                index = 2;
                break;
            case "C":
                index = 3;
                break;
            case "D":
                index = 4;
                break;
        }

        try {
            if (index != -1) {
                price = priceList.get(index).getPrice();
            }
        } catch (Exception ex) {
            log.error(ex.toString());
        }

        return price;
    }

}
