/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.entity.StockUnit;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cv.inv.dao.StockUnitDao;

/**
 *
 * @author Lenovo
 */
@Service
@Transactional
public class StockUnitServiceImpl implements StockUnitService {

    @Autowired
    private StockUnitDao dao;

    @Override
    public StockUnit save(StockUnit unit) {
        return dao.save(unit);
    }

    @Override
    public List<StockUnit> findAll() {
        return dao.findAll();
    }

    @Override
    public int delete(String id) {
        return dao.delete(id);
    }

}
