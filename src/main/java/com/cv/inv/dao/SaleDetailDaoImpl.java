/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.accountswing.dao.AbstractDao;
import com.cv.inv.entity.SaleHisDetail;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Repository
public class SaleDetailDaoImpl extends AbstractDao<String, SaleHisDetail> implements SaleDetailDao {

    @Override
    public SaleHisDetail save(SaleHisDetail sdh) {
        persist(sdh);
        return sdh;
    }

    @Override
    public List<SaleHisDetail> search(String vouId) {
        String hsql = "select o from SaleHisDetail o where o.saleDetailKey.vouId ='" + vouId + "'";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        String strSql = "delete from SaleDetailHis o where o.saleDetailKey.saleDetailId = " + id;
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }

}
