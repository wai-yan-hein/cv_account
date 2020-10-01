/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.TraderTypeDao;
import com.cv.accountswing.entity.TraderType;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Lenovo
 */
@Transactional
@Service
public class TraderTypeServiceImpl implements TraderTypeService {

    @Autowired
    private TraderTypeDao dao;

    @Override
    public TraderType save(TraderType ch) {
        return dao.save(ch);
    }

    @Override
    public List<TraderType> findAll() {
        return dao.findAll();
    }

    @Override
    public int delete(String id) {
        return dao.delete(id);
    }

    @Override
    public TraderType findById(String id) {
        return dao.findById(id);
    }

}
