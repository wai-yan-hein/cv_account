/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.accountswing.dao.AbstractDao;
import com.cv.inv.entity.StockUnit;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class StockUnitDaoImpl extends AbstractDao<String, StockUnit> implements StockUnitDao {

    @Override
    public StockUnit save(StockUnit item) {
        persist(item);
        return item;
    }

    @Override
    public List<StockUnit> findAll() {
        String hsql = "select o from StockUnit o";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        String hsql = "delete from StockUnit o where o.itemUnitCode='" + id + "'";
        return execUpdateOrDelete(hsql);
    }

}
