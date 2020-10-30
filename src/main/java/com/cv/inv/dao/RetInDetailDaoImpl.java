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
 * @author Lenovo
 */
@Repository
public class RetInDetailDaoImpl extends AbstractDao<String, RetInDetailHis> implements RetInDetailDao {

    @Override
    public RetInDetailHis save(RetInDetailHis pd) {
        persist(pd);
        return pd;
    }

    @Override
    public List<RetInDetailHis> search(String glId) {
        String strFilter = "";
        if (!glId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "v.inCompoundKey.vouNo = '" + glId + "'";
            } else {
                strFilter = strFilter + " and v.inCompoundKey.vouNo = '" + glId + "'";
            }
        }
        String strSql = "select v from RetInDetailHis v";

        List<RetInDetailHis> listDH = null;
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
            listDH = findHSQL(strSql);
        }

        return listDH;
    }

    @Override
    public int delete(String id) {
        String strSql = "delete from RetInDetailHis o where o.inCompoundKey.retInDetailId = '" + id + "'";
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }
}
