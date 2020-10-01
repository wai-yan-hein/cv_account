/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.SeqTableDao;
import com.cv.accountswing.entity.SeqTable;
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
public class SeqTableServiceImpl implements SeqTableService{
    
    @Autowired
    SeqTableDao dao;
    
    @Override
    public SeqTable save(SeqTable st){
        st = dao.save(st);
        return st;
    }
    
    @Override
    public SeqTable findById(Integer id){
        SeqTable st = dao.findById(id);
        return st;
    }
    
    @Override
    public List<SeqTable> search(String option, String period, String compCode){
        List<SeqTable> listST = dao.search(option, period, compCode);
        return listST;
    }
    
    @Override
    public SeqTable getSeqTable(String option, String period, String compCode){
        SeqTable st = dao.getSeqTable(option, period, compCode);
        return st;
    }
    
    @Override
    public int delete(Integer id){
        int cnt = dao.delete(id);
        return cnt;
    }
    
    @Override
    public int getSequence(String option, String period, String compCode){
        int seq = dao.getSequence(option, period, compCode);
        return seq;
    }
}
