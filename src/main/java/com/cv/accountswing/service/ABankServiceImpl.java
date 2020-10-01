/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.ABankDao;
import com.cv.accountswing.entity.ABank;
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
public class ABankServiceImpl implements ABankService{
    
    @Autowired
    private ABankDao dao;
    
    @Override
    public ABank save(ABank bank){
        bank = dao.save(bank);
        return bank;
    }
    
    @Override
    public ABank findById(String id){
        return dao.findById(id);
    }
    
    @Override
    public List<ABank> search(String code, String name, String address, String phone,
            String compId){
        List<ABank> listBank = dao.search(code, name, address, phone, compId);
        return listBank;
    }
    
    @Override
    public int delete(String code, String compId){
        int cnt = dao.delete(code, compId);
        return cnt;
    }
}
