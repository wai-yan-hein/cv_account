/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.inv.entity.StockBalanceTmp;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface StockBalanceTmpDao {

    public StockBalanceTmp save(StockBalanceTmp balance);

    public List<StockBalanceTmp> search(String machineId);

}
