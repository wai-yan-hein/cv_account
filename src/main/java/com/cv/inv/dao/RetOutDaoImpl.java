/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.accountswing.dao.AbstractDao;
import com.cv.inv.entity.RetOutDetailHis;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author lenovo
 */
@Repository
public class RetOutDaoImpl extends AbstractDao<String, RetOutDetailHis> implements RetOutDao {

    @Override
    public RetOutDetailHis save(RetOutDetailHis retOutDetailHis) {
        persist(retOutDetailHis);
        return retOutDetailHis;
    }

    @Override
    public List<RetOutDetailHis> search(String glId, String vouNo) {
        String strSql = "select o from RetOutDetailHis o";
        String strFilter = "";

        if (!glId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.outCompoundKey.glId = '" + glId + "'";
            } else {
                strFilter = strFilter + " and o.outCompoundKey.glId = '" + glId + "'";
            }
        }

        if (!vouNo.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.outCompoundKey.vouNo = '" + vouNo + "'";
            } else {
                strFilter = strFilter + " and o.outCompoundKey.vouNo = '" + vouNo + "'";
            }
        }
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }
        strSql = strSql + " order by o.uniqueId";
        List<RetOutDetailHis> list = findHSQL(strSql);
        return list;

    }

    @Override
    public void delete(String retOutId, String glId) {
        String strSql = "delete from RetOutDetailHis o";
        String strFilter = "";

        if (!retOutId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.outCompoundKey.retOutDetailId in (" + retOutId + ")";
            } else {
                strFilter = strFilter + " and o.outCompoundKey.retOutDetailId in (" + retOutId + ")";
            }
        }

        if (!glId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.outCompoundKey.glId = '" + glId + "'";
            } else {
                strFilter = strFilter + " and o.outCompoundKey.glId = '" + glId + "'";
            }
        }

        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }
        execUpdateOrDelete(strSql);

    }

}
