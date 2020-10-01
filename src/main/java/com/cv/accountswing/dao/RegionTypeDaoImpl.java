/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.RegionType;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author WSwe
 */
@Repository
public class RegionTypeDaoImpl extends AbstractDao<String, RegionType> implements RegionTypeDao{
    
    @Override
    public RegionType save(RegionType regionType){
        persist(regionType);
        return regionType;
    }
    
    @Override
    public RegionType findById(String id){
        RegionType regionType = getByKey(id);
        return regionType;
    }
    
    @Override
    public List<RegionType> search(String code, String name){
        String strSql = "select o from RegionType o ";
        String strFilter = "";
        
        if(!code.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.typeId like '" + code + "%'";
            }else{
                strFilter = strFilter + " and o.typeId like '" + code + "%'";
            }
        }
        
        if(!name.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.regTypeNameEng like '%" + name + "%'";
            }else{
                strFilter = strFilter + " and o.regTypeNameEng like '%" + name + "%'";
            }
        }
        
        if(!strFilter.isEmpty()){
            strSql = strSql + " where " + strFilter;
        }
        
        List<RegionType> listRT = findHSQL(strSql);
        return listRT;
    }
    
    @Override
    public int delete(String code){
        String strSql = "delete from RegionType o where o.typeId = '" + code + "'";
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }
}
