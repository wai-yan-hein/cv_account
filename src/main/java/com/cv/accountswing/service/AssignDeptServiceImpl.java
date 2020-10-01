/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.AssignDeptDao;
import com.cv.accountswing.entity.AssignDept;
import com.cv.accountswing.entity.AssignDeptKey;
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
public class AssignDeptServiceImpl implements AssignDeptService{
    
    @Autowired
    private AssignDeptDao dao;
    
    @Override
    public AssignDept save(AssignDept ad){
        return dao.save(ad);
    }
    
    @Override
    public AssignDept findById(AssignDeptKey key){
        return dao.findById(key);
    }
    
    @Override
    public List search(String compId, String roleId){
        return dao.search(compId, roleId);
    }
    
    @Override
    public int delete(String compId, String roleId, String deptCode){
        return dao.delete(compId, roleId, deptCode);
    }
    
    @Override
    public void updateNew(String compId, String roleId) throws Exception{
        dao.updateNew(compId, roleId);
    }
}
