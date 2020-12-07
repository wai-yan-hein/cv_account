/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.accountswing.dao.AbstractDao;
import com.cv.inv.entity.StockBalanceTmp;
import com.cv.inv.entity.StockBalanceTmpKey;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class StockBalanceTmpDaoImpl extends AbstractDao<StockBalanceTmpKey, StockBalanceTmp> implements StockBalanceTmpDao {

    @Override
    public StockBalanceTmp save(StockBalanceTmp balance) {
        persist(balance);
        return balance;
    }

    @Override
    public List<StockBalanceTmp> search(String machineId) {
        String hsql = "select o from StockBalanceTmp o where o.machineId = " + machineId + "";
        return findHSQL(hsql);
    }

}
