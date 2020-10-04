/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.accountswing.dao.GlDao;
import com.cv.inv.dao.RetInDao;
import com.cv.accountswing.entity.Gl;
import com.cv.inv.entity.RetInCompoundKey;
import com.cv.inv.entity.RetInDetailHis;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author lenovo
 */
@Service
@Transactional
public class RetInServiceImpl implements RetInService {

    private static final Logger logger = LoggerFactory.getLogger(RetInServiceImpl.class);
    RetInDetailHis retInDetailHis = null;

    @Autowired
    private RetInDao retInDao;

    @Autowired
    private GlDao glDao;

    @Override
    public RetInDetailHis save(Gl saveGl, List<RetInDetailHis> listRetIn) {
        int count = 1;
        String retInDetailId;
        
        
        try {
            Gl gl = glDao.save(saveGl);

            for (RetInDetailHis detailHis : listRetIn) {
                if (detailHis.getStock() != null) {
                    retInDetailId = gl.getVouNo() + detailHis.getStock().getStockCode() + '-' + count;
                    detailHis.setInCompoundKey(new RetInCompoundKey(retInDetailId,gl.getGlId(), gl.getVouNo()));
                    retInDao.save(detailHis);
                    count += 1;
                    //retInDetailHis = detailHis;
                }

            }

        } catch (Exception ex) {
            logger.error("saveRetIn : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());

        }
        return retInDetailHis;

    }

    @Override
    public void delete(String retInId) {
        }

    @Override
    public List search(String fDate, String tDate, String cusId, String locId, String vouNo, String stockCodes, String splitId, String tranSource, String compCode) {
        return retInDao.search(fDate, tDate, cusId, locId, vouNo, stockCodes, splitId, tranSource, compCode);
    }

   

}
