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
        String strFilter = "";
        if (!glId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "v.purDetailKey.vouId = '" + glId + "'";
            } else {
                strFilter = strFilter + " and v.purDetailKey.vouId = '" + glId + "'";
            }
        }
        String strSql = "select v from PurchaseDetail v";

        List<PurchaseDetail> listDH = null;
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
            listDH = findHSQL(strSql);
        }

        return listDH;
    }

    @Override
    public int delete(String id) {
        String strSql = "delete from PurchaseDetail o where o.purDetailKey.purDetailId = '" + id + "'";
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }
}
