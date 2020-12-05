/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.dao.UnitPatternDao;
import com.cv.inv.entity.UnitPattern;
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
public class UnitPatterServiceImpl implements UnitPatternService {

    @Autowired
    private UnitPatternDao dao;

    @Override
    public UnitPattern save(UnitPattern pattern) {
        return dao.save(pattern);
    }

    @Override
    public List<UnitPattern> findAll() {
        return dao.findAll();
    }

    @Override
    public int delete(String id) {
        return dao.delete(id);
    }

}
