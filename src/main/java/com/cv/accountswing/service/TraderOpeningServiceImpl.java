/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.TraderOpeningDao;
import com.cv.accountswing.entity.TraderOpeningH;
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
public class TraderOpeningServiceImpl implements TraderOpeningService{
    
    @Autowired
    private TraderOpeningDao dao;
    
    @Override
    public TraderOpeningH save(TraderOpeningH toh){
        return dao.save(toh);
    }
    
    @Override
    public TraderOpeningH findById(Long id){
        return dao.findById(id);
    }
    
    @Override
    public List<TraderOpeningH> search(String from, String to, String compCode,
            String remark){
        return dao.search(from, to, compCode, remark);
    }
    
    @Override
    public void generateZero(String tranIdH, String compCode, String currCode)throws Exception{
        dao.generateZero(tranIdH, compCode, currCode);
    }
}
