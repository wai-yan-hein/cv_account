/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.BusinessTypeDao;
import com.cv.accountswing.entity.BusinessType;
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
public class BusinessTypeServiceImpl implements BusinessTypeService{
    
    @Autowired
    private BusinessTypeDao dao;
    
    @Override
    public BusinessType save(BusinessType bt){
        return dao.save(bt);
    }
    
    @Override
    public BusinessType findById(Integer id){
        return dao.findById(id);
    }
    
    @Override
    public List<BusinessType> getAllBusinessType(){
        List<BusinessType> listCT = dao.getAllBusinessType();
        return listCT;
    }
    
    @Override
    public int delete(String id){
        return dao.delete(id);
    }
}
