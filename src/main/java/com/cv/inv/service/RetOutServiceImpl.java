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

        String retOutDetailId;
        RetOutCompoundKey key;

        try {
            Gl gl = glDao.save(saveGl);

            for (RetOutDetailHis detailHis : listRetOut) {
                if (detailHis.getStock() != null) {
                    if (detailHis.getOutCompoundKey() == null) {
                        retOutDetailId = gl.getVouNo() + '-' + detailHis.getUniqueId();
                        key = new RetOutCompoundKey(retOutDetailId, gl.getGlId(), gl.getVouNo());
                    } else {
                        retOutDetailId = detailHis.getOutCompoundKey().getRetOutDetailId();
                        key = new RetOutCompoundKey(retOutDetailId, gl.getGlId(), gl.getVouNo());

                    }
                    detailHis.setOutCompoundKey(key);
                    retOutDao.save(detailHis);
                    retOutDetailHis = detailHis;
                }

            }

        } catch (Exception ex) {
            logger.error("saveRetOut : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());

        }
        return retOutDetailHis;

    }

    @Override
    public List<RetOutDetailHis> search(String glId, String vouNo) {
        return  retOutDao.search(glId, vouNo);
    }

    @Override
    public void delete(String retOutId, String glId) {
        retOutDao.delete(retOutId, glId);
    }

   

}
