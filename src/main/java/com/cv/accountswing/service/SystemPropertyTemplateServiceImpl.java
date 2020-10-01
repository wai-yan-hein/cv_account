/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.SystemPropertyTemplateDao;
import com.cv.accountswing.entity.SystemPropertyTemplate;
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
public class SystemPropertyTemplateServiceImpl implements SystemPropertyTemplateService{
    
    @Autowired
    private SystemPropertyTemplateDao dao;
    
    @Override
    public SystemPropertyTemplate save(SystemPropertyTemplate spt){
        return dao.save(spt);
    }
    
    @Override
    public List<SystemPropertyTemplate> search(String propKey, String compType){
        return dao.search(propKey, compType);
    }
    
    @Override
    public int delete(String id){
        return dao.delete(id);
    }
}
