/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.entity.RetOutHisDetail;
import com.cv.inv.entity.RetOutHis;
import java.util.List;

/**
 *
 * @author lenovo
 */
public interface RetOutService {

    public void save(RetOutHis retIn, List<RetOutHisDetail> listRetIn, List<String> delList);

    public void delete(String retInId);

    public List<RetOutHis> search(String fromDate, String toDate, String cusId, String locId, String vouNo, String filterCode);

    public RetOutHis findById(String id);
}
