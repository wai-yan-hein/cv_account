/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.accountswing.dao.AbstractDao;
import com.cv.inv.entity.CompoundKey;
import com.cv.inv.entity.VouId;
import org.springframework.stereotype.Repository;

/**
 *
 * @author lenovo
 */
@Repository
public class VouIdDaoImpl extends AbstractDao<String, VouId> implements VouIdDao {

    @Override
    public VouId save(VouId vouId) {
        persist(vouId);
        return vouId;
    }

    @Override
    public Object getMax(String machineName, String vouType, String vouPeriod) throws Exception {

        String strSQl = "select max(o.vouNo) from VouId o where o.key.machineName='" 
                + machineName + "' and  o.key.vouType='" + vouType + "' and o.key.period ='" + vouPeriod + "'";
        Object obj = exeSQL(strSQl);
        return obj;
    }

    @Override
    public Object find(CompoundKey key) {
        Object obj = findByKey(VouId.class, key);
        return obj;
        }

}
