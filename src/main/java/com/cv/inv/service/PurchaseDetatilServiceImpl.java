/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.dao.PurchaseHisDao;
import com.cv.inv.dao.PurchaseDetailDao;
import com.cv.inv.entity.PurDetailKey;
import com.cv.inv.entity.PurHis;
import com.cv.inv.entity.PurchaseDetail;
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
public class PurchaseDetatilServiceImpl implements PurchaseDetailService {

    private static final Logger logger = LoggerFactory.getLogger(RetInServiceImpl.class);

    @Autowired
    private PurchaseDetailDao dao;
    @Autowired
    private PurchaseHisDao glDao;

    @Override
    public PurchaseDetail save(PurchaseDetail pd) {

        return dao.save(pd);
    }

    @Override
    public List<PurchaseDetail> search(String glId) {
        return dao.search(glId);
    }

    @Override
    public void save(PurHis gl, List<PurchaseDetail> listPD, List<String> delList) {
        String retInDetailId;

        try {

            if (delList != null) {
                for (String detailId : delList) {
                    dao.delete(detailId);
                }
            }
            glDao.save(gl);
            String vouNo = gl.getPurInvId();
            for (PurchaseDetail pd : listPD) {
                if (pd.getStock() != null) {
                    if (pd.getPurDetailKey() != null) {
                        pd.setPurDetailKey(pd.getPurDetailKey());
                    } else {
                        retInDetailId = vouNo + '-' + pd.getUniqueId();
                        pd.setPurDetailKey(new PurDetailKey(vouNo, retInDetailId));
                    }
                  //  pd.setLocation(gl.getLocationId());
                    dao.save(pd);
                }
            }

        } catch (Exception ex) {
            logger.error("savePurchaseDetail : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());

        }
    }

}
