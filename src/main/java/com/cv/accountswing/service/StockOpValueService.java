/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.entity.StockOpValue;
import com.cv.accountswing.entity.StockOpValueKey;
import java.util.List;

/**
 *
 * @author winswe
 */
public interface StockOpValueService {
    public StockOpValue save(StockOpValue value, String userId);
    public StockOpValue findById(StockOpValueKey key);
    public List search(String from, String to, String coaCode, String currency,
            String dept, String compId);
    public void backup(String tranDate, String coaCode, String dept, String currency,
            String compId, String userId, String option) throws Exception;
    public int delete(String tranDate, String coaCode, String dept, String currency,
            String compId, String userId);
}
