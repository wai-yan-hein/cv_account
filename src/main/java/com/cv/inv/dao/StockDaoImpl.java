/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.accountswing.dao.AbstractDao;
import com.cv.inv.entity.Stock;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class StockDaoImpl extends AbstractDao<String, Stock> implements StockDao {

    @Override
    public Stock save(Stock stock) {
        persist(stock);
        return stock;
    }

    @Override
    public Stock findById(String id) {
        return getByKey(id);
    }

    @Override
    public List<Stock> findAll() {
        String hsql = "select o from Stock o";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        String hsql = "delete from Stock o where o.stock_code = '" + id + "'";
        return execUpdateOrDelete(hsql);
    }

    @Override
    public Object getMax(String sql) {
        return getAggregate(sql);
    }

    @Override
    public List<Stock> findActiveStock() {
        String hsql = "select o from Stock o where o.isActive=true";
        return findHSQL(hsql);
        
    }

}
