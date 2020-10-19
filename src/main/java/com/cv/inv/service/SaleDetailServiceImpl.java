/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.accountswing.dao.GlDao;
import com.cv.accountswing.entity.Department;
import com.cv.accountswing.entity.Gl;
import com.cv.inv.dao.SaleDetailDao;
import com.cv.inv.dao.SaleHisDao;
import com.cv.inv.entity.SaleDetailHis;
import com.cv.inv.entity.SaleDetailKey;
import com.cv.inv.entity.SaleHis;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Service
@Transactional
public class SaleDetailServiceImpl implements SaleDetailService {

    private static final Logger logger = LoggerFactory.getLogger(SaleDetailServiceImpl.class);

    @Autowired
    private SaleDetailDao dao;

    @Autowired
    private SaleHisDao hisDao;

    @Override
    public SaleDetailHis save(SaleDetailHis sdh) {
        return dao.save(sdh);
    }

    @Override
    public List<SaleDetailHis> search(String glId) {
        return dao.search(glId);
    }

    @Override
    public void save(SaleHis saleHis, List<SaleDetailHis> listSaleDetail) throws Exception {

        SaleHis saveSaleHis = hisDao.save(saleHis);
        try {
            for (SaleDetailHis sdh : listSaleDetail) {
                if (sdh.getStock().getStockCode() != null) {
                    String vouNo = saveSaleHis.getVouNo();
                    sdh.setVouNo(vouNo);
                    dao.save(sdh);
                }
            }
        } catch (Exception ex) {
            logger.error("saveSaleDetail : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());

        }
    }
}
