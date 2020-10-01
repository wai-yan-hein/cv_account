/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.BusinessType;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author winswe
 */
@Repository
public class BusinessTypeDaoImpl extends AbstractDao<Integer, BusinessType> implements BusinessTypeDao{
    
    @Override
    public BusinessType save(BusinessType bt){
        persist(bt);
        return bt;
    }
    
    @Override
    public BusinessType findById(Integer id){
        BusinessType bt = getByKey(id);
        return bt;
    }
    
    @Override
    public List<BusinessType> getAllBusinessType(){
        List<BusinessType> listCT = findHSQL("select o from BusinessType o");
        return listCT;
    }
    
    @Override
    public int delete(String id){
        String strSql = "delete from BusinessType o where o.id = " + id;
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }
}
