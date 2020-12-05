/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.dao.RetOutDetailDao;
import com.cv.inv.entity.RetInHisDetail;
import com.cv.inv.entity.RetOutHisDetail;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Lenovo
 */
@Service
@Transactional
public class RetOutDetatilServiceImpl implements RetOutDetailService {

    private static final Logger logger = LoggerFactory.getLogger(RetOutServiceImpl.class);

    @Autowired
    private RetOutDetailDao dao;

    @Override
    public RetOutHisDetail save(RetOutHisDetail pd) {

        return dao.save(pd);
    }

    @Override
    public List<RetOutHisDetail> search(String glId) {
        return dao.search(glId);
    }

}
