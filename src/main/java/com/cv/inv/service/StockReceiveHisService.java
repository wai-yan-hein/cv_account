/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.entity.DamageHis;
import com.cv.inv.entity.StockReceiveDetailHis;
import com.cv.inv.entity.StockReceiveHis;
import java.util.List;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
public interface StockReceiveHisService {

    public StockReceiveHis save(StockReceiveHis sdh);

    public List<StockReceiveHis> search(String from, String to, String location,
            String remark, String vouNo);

    public void save(StockReceiveHis sdh, List<StockReceiveDetailHis> listDamageDetail, String vouStatus, List<String> delList);

    public StockReceiveHis findById(String id);

    public int delete(String vouNo);
}
