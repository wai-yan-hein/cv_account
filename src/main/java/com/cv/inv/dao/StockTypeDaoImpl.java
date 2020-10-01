/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.accountswing.dao.AbstractDao;
import com.cv.inv.entity.StockType;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class StockTypeDaoImpl extends AbstractDao<String, StockType> implements StockTypeDao {

    @Override
    public StockType save(StockType item) {
        persist(item);
        return item;
    }

    @Override
    public List<StockType> findAll() {
        String hsql = "select o from StockType o";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        String hsql = "delete from StockType o where o.itemTypeCode='" + id + "'";
        return execUpdateOrDelete(hsql);
    }

}
