/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.accountswing.dao.AbstractDao;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.PurHis;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Repository
public class PurchaseHisDaoImpl extends AbstractDao<String, PurHis> implements PurchaseHisDao {

    @Override
    public PurHis save(PurHis sh) {
        persist(sh);
        return sh;
    }

    @Override
    public List<PurHis> search(String fromDate, String toDate, String cusId, String vouStatusId, String remark) {
        String strFilter = "";

        if (!fromDate.equals("-") && !toDate.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "date(o.purDate) between '" + fromDate
                        + "' and '" + toDate + "'";
            } else {
                strFilter = strFilter + " and date(o.purDate) between '"
                        + fromDate + "' and '" + toDate + "'";
            }
        } else if (!fromDate.endsWith("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "date(o.purDate) >= '" + fromDate + "'";
            } else {
                strFilter = strFilter + " and date(o.purDate) >= '" + fromDate + "'";
            }
        } else if (!toDate.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "date(o.purDate) <= '" + toDate + "'";
            } else {
                strFilter = strFilter + " and date(o.purDate) <= '" + toDate + "'";
            }
        }

        if (!cusId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.customerId = '" + cusId + "'";
            } else {
                strFilter = strFilter + " and o.customerId = '" + cusId + "'";
            }
        }

        if (!vouStatusId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.vouStatus = '" + vouStatusId + "'";
            } else {
                strFilter = strFilter + " and o.vouStatus = '" + vouStatusId + "'";
            }
        }

        if (!remark.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.remark = '" + remark + "'";
            } else {
                strFilter = strFilter + " and o.remark = '" + remark + "'";
            }
        }

        String strSql = "select o from PurHis o";
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }

        List<PurHis> listPurHis = findHSQL(strSql);
        return listPurHis;
    }

    @Override
    public PurHis findById(String id) {
        PurHis ph = getByKey(id);
        return ph;
    }

    @Override
    public int delete(String vouNo) {
        String strSql1 = "delete from PurchaseDetail o where o.purDetailKey.vouId = '" + vouNo + "'";
        execUpdateOrDelete(strSql1);
        String strSql = "delete from PurHis o where o.purInvId = '" + vouNo + "'";
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }

}
