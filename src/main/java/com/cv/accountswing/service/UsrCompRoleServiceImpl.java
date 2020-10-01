/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.UsrCompRoleDao;
import com.cv.accountswing.entity.UsrCompRole;
import com.cv.accountswing.entity.UsrCompRoleKey;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author winswe
 */
@Service
@Transactional
public class UsrCompRoleServiceImpl implements UsrCompRoleService{
    
    @Autowired
    private UsrCompRoleDao dao;
    
    @Override
    public UsrCompRole save(UsrCompRole ucr){
        return dao.save(ucr);
    }
    
    @Override
    public UsrCompRole findById(UsrCompRoleKey key){
        return dao.findById(key);
    }
    
    @Override
    public List<UsrCompRole> search(String userId, String compCode, String roleId){
        return dao.search(userId, compCode, roleId);
    }
    
    @Override
    public List getAssignRole(String userId){
        return dao.getAssignRole(userId);
    }
    
    @Override
    public List getAssignCompany(String userId){
        return dao.getAssignCompany(userId);
    }
    
    @Override
    public List getAssignCompany(String userId, String roleId, String compId){
        return dao.getAssignCompany(userId, roleId, compId);
    }
    
    @Override
    public int delete(String userId, String compCode, String roleId){
        return dao.delete(userId, compCode, roleId);
    }
}
