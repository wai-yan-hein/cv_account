/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.RegionTypeDao;
import com.cv.accountswing.entity.RegionType;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author WSwe
 */
@Service
@Transactional
public class RegionTypeServiceImpl implements RegionTypeService{
    
    @Autowired
    RegionTypeDao dao;
    
    @Override
    public RegionType save(RegionType rgt){
        rgt = dao.save(rgt);
        return rgt;
    }
    
    @Override
    public RegionType findById(String id){
        RegionType rt = dao.findById(id);
        return rt;
    }
    
    @Override
    public List<RegionType> search(String code, String name){
        List<RegionType> listRT = dao.search(code, name);
        return listRT;
    }
    
    @Override
    public int delete(String code){
        int cnt = dao.delete(code);
        return cnt;
    }
}
