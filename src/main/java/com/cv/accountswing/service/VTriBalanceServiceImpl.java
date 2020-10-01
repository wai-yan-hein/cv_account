/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.VTriBalanceDao;
import com.cv.accountswing.entity.view.VTriBalance;
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
public class VTriBalanceServiceImpl implements VTriBalanceService{
    
    @Autowired
    private VTriBalanceDao dao;
    
    @Override
    public List<VTriBalance> getTriBalance(String userId, String compCode){
        return dao.getTriBalance(userId, compCode);
    }
}
