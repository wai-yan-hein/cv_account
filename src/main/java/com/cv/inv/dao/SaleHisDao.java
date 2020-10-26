/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.inv.entity.SaleHis;
import java.util.List;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
public interface SaleHisDao {

    public SaleHis save(SaleHis sh);

    public List<SaleHis> search(String fromDate, String toDate, String cusId,
            String vouStatusId, String remark, String stockCode, String userId,String machId);

    public SaleHis findById(String id);

    public int delete(String vouNo);
}
