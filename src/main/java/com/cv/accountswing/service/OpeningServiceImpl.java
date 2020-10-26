/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.OpeningDao;
import com.cv.accountswing.entity.COAOpening;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Lenovo
 */
@Service
@Transactional
public class OpeningServiceImpl implements OpeningService {

    @Autowired
    private OpeningDao dao;

    @Override
    public COAOpening save(COAOpening coa) {
        return dao.save(coa);
    }

}
