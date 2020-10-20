/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.accountswing.dao.AbstractDao;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.SaleHis;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Repository
public class SaleHisDaoImpl extends AbstractDao<String, SaleHis> implements SaleHisDao {

    @Override
    public SaleHis save(SaleHis sh) {
        persist(sh);
        return sh;
    }

    @Override
    public List<SaleHis> search(String fromDate, String toDate, String cusId, String vouStatusId, String remark) {
        String strFilter = "";

        if (!fromDate.equals("-") && !toDate.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.saleDate between '" + Util1.toDateStrMYSQL(fromDate, "dd/MM/yyyy")
                        + "' and '" + Util1.toDateStrMYSQL(toDate, "dd/MM/yyyy") + "'";
            } else {
                strFilter = strFilter + " and o.saleDate between '"
                        + Util1.toDateStrMYSQL(fromDate, "dd/MM/yyyy") + "' and '" + Util1.toDateStrMYSQL(toDate, "dd/MM/yyyy") + "'";
            }
        } else if (!fromDate.endsWith("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.saleDate >= '" + Util1.toDateStrMYSQL(fromDate, "dd/MM/yyyy") + "'";
            } else {
                strFilter = strFilter + " and o.saleDate >= '" + Util1.toDateStrMYSQL(fromDate, "dd/MM/yyyy") + "'";
            }
        } else if (!toDate.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.saleDate <= '" + Util1.toDateStrMYSQL(toDate, "dd/MM/yyyy") + "'";
            } else {
                strFilter = strFilter + " and o.saleDate <= '" + Util1.toDateStrMYSQL(toDate, "dd/MM/yyyy") + "'";
            }
        }

        if (!cusId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.traderId = '" + cusId + "'";
            } else {
                strFilter = strFilter + " and o.traderId = '" + cusId + "'";
            }
        }

        if (!vouStatusId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.vouStatusId = '" + vouStatusId + "'";
            } else {
                strFilter = strFilter + " and o.vouStatusId = '" + vouStatusId + "'";
            }
        }

        if (!remark.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.remark = '" + remark + "'";
            } else {
                strFilter = strFilter + " and o.remark = '" + remark + "'";
            }
        }

        String strSql = "select o from SaleHis o";
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }

        List<SaleHis> listSaleHis = findHSQL(strSql);
        return listSaleHis;
    }

}