/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.accountswing.dao.AbstractDao;
import com.cv.inv.entity.RetInDetailHis;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author lenovo
 */
@Repository
public class RetInDaoImpl extends AbstractDao<String, RetInDetailHis> implements RetInDao {

    @Override
    public RetInDetailHis save(RetInDetailHis retInDetailHis) {
        persist(retInDetailHis);
        return retInDetailHis;
    }

    @Override
    public void delete(String retInId, String glId) {
        String strSql = "delete from RetInDetailHis o";
        String strFilter = "";

        if (!retInId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.inCompoundKey.retInDetailId in (" + retInId + ")";
            } else {
                strFilter = strFilter + " and o.inCompoundKey.retInDetailId in (" + retInId + ")";
            }
        }

        if (!glId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.inCompoundKey.glId = '" + glId + "'";
            } else {
                strFilter = strFilter + " and o.inCompoundKey.glId = '" + glId + "'";
            }
        }

        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }
        execUpdateOrDelete(strSql);
    }

    @Override
    public List<RetInDetailHis> search(String glId, String vouNo) {
        String strSql = "select o from RetInDetailHis o";
        String strFilter = "";

        if (!glId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.inCompoundKey.glId = '" + glId + "'";
            } else {
                strFilter = strFilter + " and o.inCompoundKey.glId = '" + glId + "'";
            }
        }

        if (!vouNo.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.inCompoundKey.vouNo = '" + vouNo + "'";
            } else {
                strFilter = strFilter + " and o.inCompoundKey.vouNo = '" + vouNo + "'";
            }
        }
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }
        strSql = strSql + " order by o.uniqueId";
        List<RetInDetailHis> list = findHSQL(strSql);
        return list;

    }

}
