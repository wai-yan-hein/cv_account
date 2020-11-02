/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.accountswing.dao.AbstractDao;
import com.cv.inv.entity.RetInDetailHis;
import com.cv.inv.entity.RetOutDetailHis;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class RetOutDetailDaoImpl extends AbstractDao<String, RetOutDetailHis> implements RetOutDetailDao {

    @Override
    public List<RetOutDetailHis> search(String glId) {
        String strFilter = "";
        if (!glId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "v.outCompoundKey.vouNo = '" + glId + "'";
            } else {
                strFilter = strFilter + " and v.outCompoundKey.vouNo = '" + glId + "'";
            }
        }
        String strSql = "select v from RetOutDetailHis v";

        List<RetOutDetailHis> listDH = null;
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
            listDH = findHSQL(strSql);
        }

        return listDH;
    }

    @Override
    public int delete(String id) {
        String strSql = "delete from RetOutDetailHis o where o.outCompoundKey.retOutDetailId = '" + id + "'";
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }

    @Override
    public RetOutDetailHis save(RetOutDetailHis pd) {
        persist(pd);
        return pd;
    }
}
