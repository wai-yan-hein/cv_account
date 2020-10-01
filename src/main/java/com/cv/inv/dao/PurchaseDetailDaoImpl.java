/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.accountswing.dao.AbstractDao;
import com.cv.inv.entity.Category;
import com.cv.inv.entity.PurchaseDetail;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class PurchaseDetailDaoImpl extends AbstractDao<String, PurchaseDetail> implements PurchaseDetailDao {

    @Override
    public PurchaseDetail save(PurchaseDetail pd) {
        persist(pd);
        return pd;
    }

    @Override
    public List<PurchaseDetail> search(String glId) {
        String strSql = "select o from PurchaseDetail o ";
        String strFilter = "";

        if (!glId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.glId = '" + glId + "'";
            } else {
                strFilter = strFilter + " and o.glId = '" + glId + "'";
            }
        }
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }
        List<PurchaseDetail> listPurchase = findHSQL(strSql);
        return listPurchase;
    }
}
