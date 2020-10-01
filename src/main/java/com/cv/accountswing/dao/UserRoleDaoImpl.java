/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.UserRole;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author winswe
 */
@Repository
public class UserRoleDaoImpl extends AbstractDao<Integer, UserRole> implements UserRoleDao{
    
    @Override
    public UserRole save(UserRole role){
        persist(role);
        return role;
    }
    
    @Override
    public UserRole findById(Integer id){
        UserRole role = getByKey(id);
        return role;
    }
    
    @Override
    public List<UserRole> search(String roleName, String compCode){
        String strSql = "select o from UserRole o ";
        String strFilter = "";
        
        if(!roleName.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.roleName like '" + roleName + "%'";
            }else{
                strFilter = strFilter + " and o.roleName like '" + roleName + "%'";
            }
        }
        
        if(!compCode.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.compCode = '" + compCode + "'";
            }else{
                strFilter = strFilter + " and o.compCode = '" + compCode + "'";
            }
        }
        
        if(!strFilter.isEmpty()){
            strSql = strSql + " where " + strFilter;
        }
        
        List<UserRole> listUserRole = findHSQL(strSql);
        return listUserRole;
    }
    
    @Override
    public int delete(String id){
        String strSql = "delete from UserRole o where o.roleId = " + id;
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }
}
