/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.accountswing.dao.GlDao;
import com.cv.accountswing.entity.Gl;
import com.cv.inv.dao.SaleDetailDao;
import com.cv.inv.entity.SaleDetailHis;
import com.cv.inv.entity.SaleDetailKey;
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
    private GlDao glDao;

    @Override
    public SaleDetailHis save(SaleDetailHis sdh) {
        return dao.save(sdh);
    }

    @Override
    public List<SaleDetailHis> search(String glId) {
        return dao.search(glId);
    }

    @Override
    public void save(Gl gl, List<SaleDetailHis> listSaleDetail) {

        String saleDetailId;

        try {
            Gl saveGL = glDao.save(gl);
            String vouNo = gl.getVouNo();
            for (SaleDetailHis sdh : listSaleDetail) {
                if (sdh.getStock().getStockCode() != null) {
                    saleDetailId = vouNo + '-' + sdh.getUniqueId();
                    sdh.setSaleDetailKey(new SaleDetailKey(vouNo, saleDetailId));
                    sdh.setGlId(saveGL.getGlId());
                    dao.save(sdh);
                }
            }

        } catch (Exception ex) {
            logger.error("saveSaleDetail : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());

        }
    }

}
