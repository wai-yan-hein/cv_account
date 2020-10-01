/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.accountswing.dao.GlDao;
import com.cv.accountswing.entity.Gl;
import com.cv.inv.dao.RetOutDao;
import com.cv.inv.entity.RetOutCompoundKey;
import com.cv.inv.entity.RetOutDetailHis;
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
public class RetOutServiceImpl implements RetOutService {

    private static final Logger logger = LoggerFactory.getLogger(RetOutServiceImpl.class);
    private RetOutDetailHis retOutDetailHis;
    @Autowired
    private RetOutDao retOutDao;
    @Autowired
    private GlDao glDao;

    @Override
    public RetOutDetailHis save(Gl saveGl, List<RetOutDetailHis> listRetOut) {
        int count = 1;
        String retOutDetailId;

        try {
            Gl gl = glDao.save(saveGl);

            for (RetOutDetailHis detailHis : listRetOut) {
                if (detailHis.getStock() != null) {
                    retOutDetailId = gl.getVouNo() + detailHis.getStock().getStockCode() + '-' + count;
                    detailHis.setOutCompoundKey(new RetOutCompoundKey(retOutDetailId, gl.getGlId(), gl.getVouNo()));
                    retOutDao.save(detailHis);
                    count += 1;
                    retOutDetailHis = detailHis;
                }

            }

        } catch (Exception ex) {
            logger.error("saveRetOut : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());

        }
        return retOutDetailHis;

    }

    @Override
    public void delete(String retOutId) {
    }

}
