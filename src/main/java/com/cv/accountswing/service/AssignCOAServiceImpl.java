/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.AssignCOADao;
import com.cv.accountswing.entity.AssignCOA;
import com.cv.accountswing.entity.AssignCOAKey;
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
public class AssignCOAServiceImpl implements AssignCOAService{
    
    @Autowired
    private AssignCOADao dao;
    
    @Override
    public AssignCOA save(AssignCOA acoa){
        return dao.save(acoa);
    }
    
    @Override
    public AssignCOA findById(AssignCOAKey key){
        return dao.findById(key);
    }
    
    @Override
    public List search(String compId, String roleId, String deptCode){
        return dao.search(compId, roleId, deptCode);
    }
    
    @Override
    public int delete(String compId, String roleId, String deptCode, String coaCode){
        return dao.delete(compId, roleId, deptCode, coaCode);
    }
    
    @Override
    public void updateNew(String compId, String roleId, String deptCode) throws Exception{
        dao.updateNew(compId, roleId, deptCode);
    }
}
