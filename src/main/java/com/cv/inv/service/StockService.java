/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.entity.StockType;
import com.cv.inv.entity.Stock;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface StockService {

    public Stock save(Stock stock, StockType item, String status);

    public Stock findById(String id);

    public List<Stock> findAll();

    public int delete(String id);

    public List<Stock> findActiveStock();

    public List<Stock> search(String stockType);

    public List<Stock> searchC(String stockCat);

    public List<Stock> searchB(String stockBrand);

}
