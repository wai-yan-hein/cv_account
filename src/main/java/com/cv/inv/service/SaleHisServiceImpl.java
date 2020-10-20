/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.dao.SaleHisDao;
import com.cv.inv.entity.SaleHis;
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
public class SaleHisServiceImpl implements SaleHisService {

    @Autowired
    private SaleHisDao hisDao;

    @Override
    public SaleHis save(SaleHis saleHis) throws Exception {
        hisDao.save(saleHis);
        return saleHis;
    }

    @Override
    public List<SaleHis> search(String fromDate, String toDate, String cusId, String vouStatusId, String remark) {
        return hisDao.search(fromDate, toDate, cusId, vouStatusId, remark);
    }

}
