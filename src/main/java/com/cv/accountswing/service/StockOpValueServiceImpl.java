/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.StockOpValueDao;
import com.cv.accountswing.entity.StockOpValue;
import com.cv.accountswing.entity.StockOpValueKey;
import com.cv.accountswing.ui.setup.ChartOfAccountSetup;
import com.cv.accountswing.util.Util1;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author winswe
 */
@Service
@Transactional
public class StockOpValueServiceImpl implements StockOpValueService {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(StockOpValueServiceImpl.class);
    
    @Autowired
    StockOpValueDao dao;
    
    @Override
    public StockOpValue save(StockOpValue value, String userId) {
        try {
            String tranDate = Util1.toDateStr(value.getKey().getTranDate(), "yyyy-MM-dd");
            String coaCode = value.getKey().getCoaCode();
            String currancy = value.getKey().getCurrency();
            String compId = value.getKey().getCompId().toString();
            String dept = value.getKey().getDeptCode();
            dao.backup(tranDate, coaCode, dept, currancy, compId, userId, "EDIT");
        } catch (Exception ex) {
            LOGGER.error("Save Stock Op Value :" + ex.getMessage());
            
        }
        
        dao.save(value);
        return value;
    }
    
    @Override
    public StockOpValue findById(StockOpValueKey key) {
        return dao.findById(key);
    }
    
    @Override
    public List search(String from, String to, String coaCode, String currency,
            String dept, String compId) {
        return dao.search(from, to, coaCode, currency, dept, compId);
    }
    
    @Override
    public void backup(String tranDate, String coaCode, String dept, String currency,
            String compId, String userId, String option) throws Exception {
        dao.backup(tranDate, coaCode, dept, currency, compId, userId, option);
    }
    
    @Override
    public int delete(String tranDate, String coaCode, String dept, String currency,
            String compId, String userId) {
        try {
            dao.backup(tranDate, coaCode, dept, currency, compId, userId, "DELETE");
        } catch (Exception ex) {
            
        }
        
        int cnt = dao.delete(tranDate, coaCode, dept, currency, compId);
        return cnt;
    }
}
