/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.accountswing.dao.AbstractDao;
import com.cv.inv.entity.RetOutHisDetail;
import com.cv.inv.entity.RetOutHis;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author lenovo
 */
@Repository
public class RetOutDaoImpl extends AbstractDao<String, RetOutHis> implements RetOutDao {

    @Override
    public RetOutHis save(RetOutHis retOutDetailHis) {
        persist(retOutDetailHis);
        return retOutDetailHis;
    }

    @Override
    public List<RetOutHis> search(String fromDate, String toDate, String cusId, String locId, String vouNo, String filterCode) {
        String strFilter = "";

        if (!fromDate.equals("-") && !toDate.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.retOutDate between '" + fromDate
                        + "' and '" + toDate + "'";
            } else {
                strFilter = strFilter + " and o.retOutDate between '"
                        + fromDate + "' and '" + toDate + "'";
            }
        } else if (!fromDate.endsWith("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.retOutDate >= '" + fromDate + "'";
            } else {
                strFilter = strFilter + " and o.retOutDate >= '" + fromDate + "'";
            }
        } else if (!toDate.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.retOutDate <= '" + toDate + "'";
            } else {
                strFilter = strFilter + " and o.retOutDate <= '" + toDate + "'";
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
                strFilter = "o.retOutId = '" + vouNo + "'";
            } else {
                strFilter = strFilter + " and o.retOutId = '" + vouNo + "'";
            }
        }
        if (!filterCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.remark = '" + filterCode + "'";
            } else {
                strFilter = strFilter + " and o.remark = '" + filterCode + "'";
            }
        }
        String strSql = "select o from RetOutHis o";
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }

        List<RetOutHis> listPurHis = findHSQL(strSql);
        return listPurHis;
    }

    @Override
    public RetOutHis findById(String id) {
        RetOutHis ph = getByKey(id);
        return ph;
    }
}
