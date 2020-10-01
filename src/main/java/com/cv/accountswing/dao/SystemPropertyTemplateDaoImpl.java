/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.SystemPropertyTemplate;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author winswe
 */
@Repository
public class SystemPropertyTemplateDaoImpl extends AbstractDao<Long, SystemPropertyTemplate> 
    implements SystemPropertyTemplateDao{
    
    @Override
    public SystemPropertyTemplate save(SystemPropertyTemplate spt){
        persist(spt);
        return spt;
    }
    
    @Override
    public List<SystemPropertyTemplate> search(String propKey, String compType){
        String strSql = "select o from SystemPropertyTemplate o where o.compType = " + compType;
        
        if(!propKey.equals("-")){
            strSql = strSql + " and o.propKey = '" + propKey + "'";
        }
        
        List<SystemPropertyTemplate> listSPT = findHSQL(strSql);
        return listSPT;
    }
    
    @Override
    public int delete(String id){
        String strSql = "delete from SystemPropertyTemplate o where o.tranId = " + id;
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }
}
