/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.RegionDao;
import com.cv.accountswing.entity.Region;
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
public class RegionServiceImpl implements RegionService{
    
    @Autowired
    RegionDao dao;
    
    @Override
    public Region save(Region region){
        region = dao.save(region);
        return region;
    }
    
    @Override
    public Region findById(String id){
        Region region = dao.findById(id);
        return region;
    }
    
    @Override
    public List<Region> search(String code, String name, String compId,String parentCode){
        List<Region> listRegion = dao.search(code, name, compId,parentCode);
        return listRegion;
    }
    
    @Override
    public int delete(String code, String compCode){
        int cnt = dao.delete(code, compCode);
        return cnt;
    }
}
