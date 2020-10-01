/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.CoaTemplate;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author winswe
 */
@Repository
public class CoaTemplateDaoImpl extends AbstractDao<Long, CoaTemplate> implements CoaTemplateDao{
    
    @Override
    public void addTemplate(String coaCode, String businessType){
    
    }
    
    @Override
    public CoaTemplate save(CoaTemplate coat){
        persist(coat);
        return coat;
    }
    
    @Override
    public List<CoaTemplate> search(String businessType, String coaCode){
        String strSql = "select o from CoaTemplate o where o.businessType = " + businessType;
        
        if(!coaCode.equals("-")){
            strSql = strSql + " and o.coaCode = '" + coaCode + "'";
        }
        
        List<CoaTemplate> listCOAT = findHSQL(strSql);
        return listCOAT;
    }
    
    @Override
    public int delete(String id){
        String strSql = "delete from CoaTemplate o where o.tranId = " + id;
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }
}
