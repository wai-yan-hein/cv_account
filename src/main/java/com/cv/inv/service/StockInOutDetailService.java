/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.entity.StockInOutDetail;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface StockInOutDetailService {

    public StockInOutDetail save(StockInOutDetail stock);

    public List<StockInOutDetail> search(String fromDate, String toDate, String stockCode, String locId, String option, String remark);

    public int delete(Integer id);
}
