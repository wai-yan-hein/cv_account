/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.dao.StockInOutDao;
import com.cv.inv.entity.StockInOut;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Lenovo
 */
@Service
@Transactional
public class StockInOutServiceImpl implements StockInOutService {

    @Autowired
    private StockInOutDao dao;

    @Override
    public StockInOut save(StockInOut stock) {
        return dao.save(stock);
    }

    @Override
    public List<StockInOut> search(String fromDate, String toDate, String stockCode, String locId, String option, String remark) {
        return dao.search(fromDate, toDate, stockCode, locId, option, remark);
    }

    @Override
    public int delete(Integer id) {
        return dao.delete(id);
    }

}
