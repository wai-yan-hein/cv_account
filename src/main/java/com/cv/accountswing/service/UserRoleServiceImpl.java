/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.UserRoleDao;
import com.cv.accountswing.entity.UserRole;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author winswe
 */
@Service
@Transactional
public class UserRoleServiceImpl implements UserRoleService{
    
    @Autowired
    private UserRoleDao dao;
    
    @Override
    public UserRole save(UserRole role){
        return dao.save(role);
    }
    
    @Override
    public UserRole findById(Integer id){
        return dao.findById(id);
    }
    
    @Override
    public List<UserRole> search(String roleName, String compCode){
        return dao.search(roleName, compCode);
    }
    
    @Override
    public int delete(String id){
        return dao.delete(id);
    }
    
    @Override
    public UserRole copyRole(String copyRoleId, String compCode){
        UserRole old = findById(Integer.parseInt(copyRoleId));
        UserRole newRole = new UserRole();
        
        BeanUtils.copyProperties(old, newRole);
        newRole.setRoleId(null);
        newRole.setCompCode(Integer.parseInt(compCode));
        
        return save(newRole);
    }
}
