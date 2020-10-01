/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.SystemPropertyDao;
import com.cv.accountswing.entity.SystemProperty;
import com.cv.accountswing.entity.SystemPropertyKey;
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
public class SystemPropertyServiceImpl implements SystemPropertyService{
    
    @Autowired
    SystemPropertyDao dao;
    
    @Override
    public SystemProperty save(SystemProperty sp){
        sp = dao.save(sp);
        return sp;
    }
    
    @Override
    public SystemProperty findById(SystemPropertyKey id){
        SystemProperty sp = dao.findById(id);
        return sp;
    }
    
    @Override
    public List<SystemProperty> search(String code, String compCode, String value){
        List<SystemProperty> listSP = dao.search(code, compCode, value);
        return listSP;
    }
    
    @Override
    public int delete(String code){
        int cnt = dao.delete(code);
        return cnt;
    }
    
    @Override
    public void copySystemProperty(String from, String to) throws Exception{
        dao.copySystemProperty(from, to);
    }
}
