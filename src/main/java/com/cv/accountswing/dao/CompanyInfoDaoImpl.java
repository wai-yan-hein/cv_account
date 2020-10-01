/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.CompanyInfo;
import java.util.List;
import org.springframework.stereotype.Repository;


/**
 *
 * @author WSwe
 */
@Repository
public class CompanyInfoDaoImpl extends AbstractDao<Integer, CompanyInfo> implements CompanyInfoDao{
    
    @Override
    public CompanyInfo save(CompanyInfo ci){
        persist(ci);
        return ci;
    }
    
    @Override
    public CompanyInfo findById(Integer code){
        CompanyInfo ci = getByKey(code);
        return ci;
    }
    
    @Override
    public List<CompanyInfo> search(String code, String name, String phone, String address,
            String businessType, String owner){
        String strSql = "select o from CompanyInfo o ";
        String strFilter = "";
        
        if(!code.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.compCode like '" + code + "%'";
            }else{
                strFilter = strFilter + " and o.compCode like '" + code + "%'";
            }
        }
        
        if(!name.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.name like '%" + name + "%'";
            }else{
                strFilter = strFilter + " and o.name like '%" + name + "%'";
            }
        }
        
        if(!address.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.address like '%" + address + "%'";
            }else{
                strFilter = strFilter + " and o.address like '%" + address + "%'";
            }
        }
        
        if(!phone.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.phone like '%" + phone + "%'";
            }else{
                strFilter = strFilter + " and o.phone like '%" + phone + "%'";
            }
        }
        
        if(!businessType.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.businessType = " + businessType;
            }else{
                strFilter = strFilter + " and o.businessType = " + businessType;
            }
        }
        
        if(!owner.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.owner = " + owner;
            }else{
                strFilter = strFilter + " and o.owner = " + owner;
            }
        }
        
        if(!strFilter.isEmpty()){
            strSql = strSql + " where " + strFilter;
        }
        
        List<CompanyInfo> listCI = findHSQL(strSql);
        return listCI;
    }
    
    @Override
    public int delete(String code){
        String strSql = "delete from CompanyInfo o where o.compCode = '" + code + "'";
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }
}
