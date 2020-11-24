/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.AutoTextDao;
import com.cv.accountswing.entity.AutoText;
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
public class AutoTextServiceImpl implements AutoTextService {

    @Autowired
    private AutoTextDao dao;

    @Override
    public AutoText save(AutoText autoText) {
        return dao.save(autoText);
    }

    @Override
    public List<AutoText> search(String option) {
        return dao.search(option);
    }

}
