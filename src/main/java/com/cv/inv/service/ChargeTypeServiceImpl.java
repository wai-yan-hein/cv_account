/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.dao.ChargeTypeDao;
import com.cv.inv.entity.ChargeType;
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
public class ChargeTypeServiceImpl implements ChargeTypeService{
    
    @Autowired
    private ChargeTypeDao dao;

    @Override
    public ChargeType save(ChargeType chargeType) {
        dao.save(chargeType);
        return chargeType;
    }

    @Override
    public List<ChargeType> findAll() {
        return dao.findAll();
    }

    @Override
    public int delete(String id) {
        return dao.delete(id);
    }
    
}
