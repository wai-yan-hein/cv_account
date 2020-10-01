/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.TraderOpeningDaoD;
import com.cv.accountswing.entity.TraderOpeningD;
import com.cv.accountswing.entity.view.VTmpTraderBalance;
import com.cv.accountswing.entity.view.VTraderOpeningDetail;
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
public class TraderOpeningDServiceImpl implements TraderOpeningDService{
    
    @Autowired
    private TraderOpeningDaoD dao;
    
    @Override
    public TraderOpeningD save(TraderOpeningD tod){
        return dao.save(tod);
    }
    
    @Override
    public TraderOpeningD findById(Long id){
        return dao.findById(id);
    }
    
    @Override
    public List<VTraderOpeningDetail> search(String tranIdH){
        return dao.search(tranIdH);
    }
    
    @Override
    public int delete(String tranId){
        return dao.delete(tranId);
    }
    
    @Override
    public List<VTmpTraderBalance> getTraderBalance(String compCode, String traderId, String opDate, 
            String curr, String userId) throws Exception{
        return dao.getTraderBalance(compCode, traderId, opDate, curr, userId);
    }
}
