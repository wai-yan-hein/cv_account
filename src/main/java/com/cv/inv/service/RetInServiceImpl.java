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

        String retInDetailId;
        RetInCompoundKey key;

        try {
            Gl gl = glDao.save(saveGl);

            for (RetInDetailHis detailHis : listRetIn) {
                if (detailHis.getStock() != null) {
                    if (detailHis.getInCompoundKey() == null) {
                        retInDetailId = gl.getVouNo() + '-' + detailHis.getUniqueId();
                        key = new RetInCompoundKey(retInDetailId, gl.getGlId(), gl.getVouNo());
                    } else {
                        retInDetailId = detailHis.getInCompoundKey().getRetInDetailId();
                        key = new RetInCompoundKey(retInDetailId, gl.getGlId(), gl.getVouNo());
                    }

                    detailHis.setInCompoundKey(key);
                    retInDao.save(detailHis);
<<<<<<< HEAD
                    retInDetailHis = detailHis;
=======
                    count += 1;
                    //retInDetailHis = detailHis;
>>>>>>> 2645f0594b7ed96750a08ea9446f2c4710390d82
                }

            }

        } catch (Exception ex) {
            logger.error("saveRetIn : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());

        }
        return retInDetailHis;

    }

    @Override
    public void delete(String retInId,String glId) {
        retInDao.delete(retInId, glId);
    }

    @Override
    public List<RetInDetailHis> search(String glId, String vouNo) {
        return retInDao.search(glId, vouNo);
    }

}
