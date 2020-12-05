/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.inv.entity.PurHis;
import java.util.List;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
public interface PurchaseHisDao {

    public PurHis save(PurHis sh);

    public List<PurHis> search(String fromDate, String toDate, String cusId, String vouStatusId, String remark);

    public PurHis findById(String id);

    public int delete(String vouNo);

}
