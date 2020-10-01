/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.UsrCompRole;
import com.cv.accountswing.entity.UsrCompRoleKey;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author winswe
 */
@Repository
public class UsrCompRoleDaoImpl extends AbstractDao<UsrCompRoleKey, UsrCompRole> implements UsrCompRoleDao{
    
    @Override
    public UsrCompRole save(UsrCompRole ucr){
        persist(ucr);
        return ucr;
    }
    
    @Override
    public UsrCompRole findById(UsrCompRoleKey key){
        UsrCompRole ucr = getByKey(key);
        return ucr;
    }
    
    @Override
    public List<UsrCompRole> search(String userId, String compCode, String roleId){
        String strSql = "select o from UsrCompRole o ";
        String strFilter = "";
        
        if(!userId.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.key.userId = " + userId;
            }else{
                strFilter = strFilter + " and o.key.userId = " + userId;
            }
        }
        
        if(!compCode.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.key.compCode = '" + compCode + "'";
            }else{
                strFilter = strFilter + " and o.key.compCode = '" + compCode + "'";
            }
        }
        
        if(!roleId.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.key.roleId = " + roleId;
            }else{
                strFilter = strFilter + " and o.key.roleId = " + roleId;
            }
        }
        
        if(!strFilter.isEmpty()){
            strSql = strSql + " where " + strFilter;
        }
        
        List<UsrCompRole> listUsrCompRole = findHSQL(strSql);
        return listUsrCompRole;
    }
    
    @Override
    public List getAssignRole(String userId){
        String strSql = "select o from VUsrCompRole o where o.key.userId = " + userId;
        List listAssignRole = findHSQL(strSql);
        return listAssignRole;
    }
    
    @Override
    public List getAssignCompany(String userId){
        String strSql = "select o from VUsrCompAssign o where o.key.userId = " + userId;
        List listAssignComp = findHSQL(strSql);
        return listAssignComp;
    }
    
    @Override
    public List getAssignCompany(String userId, String roleId, String compId){
        String strSql = "select o from VUsrCompAssign o where o.key.userId = " + 
                userId + " and o.key.roleId = " + roleId + " and o.key.compCode = " + compId;
        List listAssignComp = findHSQL(strSql);
        return listAssignComp;
    }
    
    @Override
    public int delete(String userId, String compCode, String roleId){
        String strSql = "delete from UsrCompRole o where o.key.userId = " + 
                userId + " and o.key.compCode = " + compCode + " and o.key.roleId = " + roleId;
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }
}
