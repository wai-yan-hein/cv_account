/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.CoaTemplateDao;
import com.cv.accountswing.entity.CoaTemplate;
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
public class CoaTemplateServiceImpl implements CoaTemplateService{
    
    @Autowired
    private CoaTemplateDao dao;
    
    @Override
    public CoaTemplate save(CoaTemplate coat){
        return dao.save(coat);
    }
    
    @Override
    public List<CoaTemplate> search(String companyType, String coaCode){
        return dao.search(companyType, coaCode);
    }
    
    @Override
    public int delete(String id){
        return dao.delete(id);
    }
}
