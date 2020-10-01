/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.accountswing.dao.AbstractDao;
import com.cv.inv.entity.RetOutDetailHis;
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
    public void delete(String retOutId) {
    }

}
