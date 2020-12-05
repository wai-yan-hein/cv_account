/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.accountswing.dao.AbstractDao;
import com.cv.inv.entity.Category;
import com.cv.inv.entity.PurHisDetail;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class PurchaseDetailDaoImpl extends AbstractDao<String, PurHisDetail> implements PurchaseDetailDao {

    @Override
    public PurHisDetail save(PurHisDetail pd) {
        persist(pd);
        return pd;
    }

    @Override
    public List<PurHisDetail> search(String vouId) {
        String hsql = "select o from PurHisDetail o where o.purDetailKey.vouId = '" + vouId + "'";
        return findHSQL(hsql);

    }

    @Override
    public int delete(String id) {
        String strSql = "delete from PurchaseDetail o where o.purDetailKey.purDetailId = '" + id + "'";
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }
}
