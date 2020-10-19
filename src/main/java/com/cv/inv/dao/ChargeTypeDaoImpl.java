/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.accountswing.dao.AbstractDao;
import com.cv.inv.entity.ChargeType;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Repository
public class ChargeTypeDaoImpl extends AbstractDao<String, ChargeType> implements ChargeTypeDao {

    @Override
    public ChargeType save(ChargeType chargeType) {
        persist(chargeType);
        return chargeType;
    }

    @Override
    public List<ChargeType> findAll() {
        String hsql = "select o from ChargeType o";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        String hsql = "delete from ChargeType o where o.chargeTypeId='" + id + "'";
        return execUpdateOrDelete(hsql);
    }

}
