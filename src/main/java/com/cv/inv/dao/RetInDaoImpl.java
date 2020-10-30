/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.accountswing.dao.AbstractDao;
import com.cv.inv.entity.RetInHis;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author lenovo
 */
@Repository
public class RetInDaoImpl extends AbstractDao<String, RetInHis> implements RetInDao {

    @Override
    public RetInHis save(RetInHis retInDetailHis) {
        persist(retInDetailHis);
        return retInDetailHis;
    }

    @Override
    public List<RetInHis> search(String fromDate, String toDate, String cusId, String locId, String vouNo, String filterCode) {
        String strFilter = "";

        if (!fromDate.equals("-") && !toDate.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.retInDate between '" + fromDate
                        + "' and '" + toDate + "'";
            } else {
                strFilter = strFilter + " and o.retInDate between '"
                        + fromDate + "' and '" + toDate + "'";
            }
        } else if (!fromDate.endsWith("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.retInDate >= '" + fromDate + "'";
            } else {
                strFilter = strFilter + " and o.retInDate >= '" + fromDate + "'";
            }
        } else if (!toDate.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.retInDate <= '" + toDate + "'";
            } else {
                strFilter = strFilter + " and o.retInDate <= '" + toDate + "'";
            }
        }

        if (!cusId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.customer = '" + cusId + "'";
            } else {
                strFilter = strFilter + " and o.customer = '" + cusId + "'";
            }
        }

        if (!locId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.location = '" + locId + "'";
            } else {
                strFilter = strFilter + " and o.location = '" + locId + "'";
            }
        }

        if (!vouNo.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.retInId = '" + vouNo + "'";
            } else {
                strFilter = strFilter + " and o.retInId = '" + vouNo + "'";
            }
        }
        if (!filterCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.remark = '" + filterCode + "'";
            } else {
                strFilter = strFilter + " and o.remark = '" + filterCode + "'";
            }
        }
        String strSql = "select o from RetInHis o";
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }

        List<RetInHis> listPurHis = findHSQL(strSql);
        return listPurHis;
    }

    @Override
    public RetInHis findById(String id) {
        RetInHis ph = getByKey(id);
        return ph;
    }

}
