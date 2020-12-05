/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.dao.RetInDao;
import com.cv.inv.dao.RetInDetailDao;
import com.cv.inv.entity.RetInCompoundKey;
import com.cv.inv.entity.RetInHisDetail;
import com.cv.inv.entity.RetInHis;
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
    RetInHisDetail retInDetailHis = null;

    @Autowired
    private RetInDao retInDao;

    @Autowired
    private RetInDetailDao dao;

    @Override
    public void save(RetInHis retIn, List<RetInHisDetail> listRetIn, List<String> delList) {

        String retInDetailId;
        RetInCompoundKey key;

        try {
            if (delList != null) {
                for (String detailId : delList) {
                    dao.delete(detailId);
                }
            }
            retInDao.save(retIn);
            String vouNo = retIn.getRetInId();
            for (RetInHisDetail rd : listRetIn) {
                if (rd.getStock() != null) {
                    if (rd.getInCompoundKey() != null) {
                        rd.setInCompoundKey(rd.getInCompoundKey());
                    } else {
                        retInDetailId = vouNo + '-' + rd.getUniqueId();
                        rd.setInCompoundKey(new RetInCompoundKey(retInDetailId, vouNo));
                    }
                    dao.save(rd);
                }

            }

        } catch (Exception ex) {
            logger.error("saveRetIn : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());

        }

    }

    @Override
    public void delete(String retInId) {
        dao.delete(retInId);
    }

    @Override
    public List<RetInHis> search(String fromDate, String toDate, String cusId, String locId, String vouNo, String filterCode) {
        return retInDao.search(fromDate, toDate, cusId, locId, vouNo, filterCode);
    }

    @Override
    public RetInHis findById(String id) {
        return retInDao.findById(id);
    }

}
