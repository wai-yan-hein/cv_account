/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.dao.PurchaseHisDao;
import com.cv.inv.dao.RetInDetailDao;
import com.cv.inv.entity.PurDetailKey;
import com.cv.inv.entity.PurHis;
import com.cv.inv.entity.PurHisDetail;
import com.cv.inv.entity.RetInHisDetail;
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
public class RetInDetatilServiceImpl implements RetInDetailService {

    private static final Logger logger = LoggerFactory.getLogger(RetInServiceImpl.class);

    @Autowired
    private RetInDetailDao dao;
    @Autowired
    private PurchaseHisDao glDao;

    @Override
    public RetInHisDetail save(RetInHisDetail pd) {

        return dao.save(pd);
    }

    @Override
    public List<RetInHisDetail> search(String glId) {
        return dao.search(glId);
    }

}
