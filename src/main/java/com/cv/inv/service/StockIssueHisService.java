/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.entity.DamageHis;
import com.cv.inv.entity.StockIssueDetailHis;
import com.cv.inv.entity.StockIssueHis;
import java.util.List;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
public interface StockIssueHisService {

    public StockIssueHis save(StockIssueHis sdh);

    public List<StockIssueHis> search(String from, String to, String location,
            String remark, String vouNo);

    public void save(StockIssueHis sdh, List<StockIssueDetailHis> listDamageDetail, String vouStatus, List<String> delList);

    public StockIssueHis findById(String id);

    public int delete(String vouNo);
}
