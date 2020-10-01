/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.temp.TmpOpeningClosingKey;
import com.cv.accountswing.entity.view.VTriBalance;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author winswe
 */
@Repository
public class VTriBalanceDaoImpl extends AbstractDao<TmpOpeningClosingKey, VTriBalance> implements VTriBalanceDao{
    
    @Override
    public List<VTriBalance> getTriBalance(String userId, String compCode){
        String strSql = "select o from VTriBalance o where o.key.userId = '" + 
                userId + "' and o.key.compCode = " + compCode;
        List<VTriBalance> listVTB = findHSQL(strSql);
        return listVTB;
    }
}
