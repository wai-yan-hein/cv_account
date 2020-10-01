/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.VCoaTreeDao;
import com.cv.accountswing.entity.view.VCoaTree;
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
public class VCoaTreeServiceImpl implements VCoaTreeService{
    
    @Autowired
    private VCoaTreeDao dao;
    
    @Override
    public List<VCoaTree> getCompanyCoa(String compId){
        return dao.getCompanyCoa(compId);
    }
}
