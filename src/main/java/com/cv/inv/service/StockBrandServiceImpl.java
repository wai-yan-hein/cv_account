/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.entity.StockBrand;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cv.inv.dao.StockBrandDao;

/**
 *
 * @author Lenovo
 */
@Service
@Transactional
public class StockBrandServiceImpl implements StockBrandService {

    @Autowired
    private StockBrandDao dao;

    @Override
    public StockBrand save(StockBrand brand) {
        return dao.save(brand);
    }

    @Override
    public List<StockBrand> findAll() {
        return dao.findAll();
    }

    @Override
    public int delete(String id) {
        return dao.delete(id);
    }

}
