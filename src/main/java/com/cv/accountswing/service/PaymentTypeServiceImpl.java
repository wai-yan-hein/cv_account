/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.PaymentTypeDao;
import com.cv.accountswing.entity.PaymentType;
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
public class PaymentTypeServiceImpl implements PaymentTypeService{
    
    @Autowired
    PaymentTypeDao dao;
    
    @Override
    public PaymentType save(PaymentType pt){
        pt = dao.save(pt);
        return pt;
    }
    
    @Override
    public PaymentType findById(Integer id){
        PaymentType pt = dao.findById(id);
        return pt;
    }
    
    @Override
    public List<PaymentType> search(String name, String compId){
        List<PaymentType> listPT = dao.search(name, compId);
        return listPT;
    }
    
    @Override
    public int delete(Integer code, String compId){
        int cnt = dao.delete(code, compId);
        return cnt;
    }
}
