/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.entity.StockType;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface StockTypeService {

    public StockType save(StockType item);

    public List<StockType> findAll();

    public int delete(String id);
}
