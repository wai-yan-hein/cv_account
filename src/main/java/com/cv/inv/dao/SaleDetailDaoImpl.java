/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.accountswing.dao.AbstractDao;
import com.cv.inv.entity.SaleDetailHis;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Repository
public class SaleDetailDaoImpl extends AbstractDao<String, SaleDetailHis> implements SaleDetailDao {

    @Override
    public SaleDetailHis save(SaleDetailHis sdh) {
        persist(sdh);
        return sdh;
    }

    @Override
    public List<SaleDetailHis> search(String glId) {
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
        List<SaleDetailHis> listSale = findHSQL(strSql);
        return listSale;
    }

}
