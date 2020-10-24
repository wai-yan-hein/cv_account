/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.dao.SaleManDao;
import com.cv.inv.entity.SaleMan;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Service
@Transactional
public class SaleManServiceImpl implements SaleManService {

    @Autowired
    private SaleManDao dao;

    @Override
    public SaleMan save(SaleMan saleMan) {
        dao.save(saleMan);
        return saleMan;
    }

    @Override
    public List<SaleMan> findAll() {
        return dao.findAll();
    }

    @Override
    public int delete(String id) {
        return dao.delete(id);
    }

    @Override
    public SaleMan findById(String id) {
        return dao.findById(id);
    }

}
