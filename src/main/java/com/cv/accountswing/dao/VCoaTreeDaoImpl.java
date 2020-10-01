/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.view.VCoaTree;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author winswe
 */
@Repository
public class VCoaTreeDaoImpl extends AbstractDao<String, VCoaTree> implements VCoaTreeDao{
    
    @Override
    public List<VCoaTree> getCompanyCoa(String compId){
        String strSql = "select o from VCoaTree o where o.compCode = " +compId;
        List<VCoaTree> listVCT = findHSQL(strSql);
        return listVCT;
    }
}
