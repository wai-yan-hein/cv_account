/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.entity.RetInHisDetail;
import com.cv.inv.entity.RetInHis;
import java.util.List;

/**
 *
 * @author lenovo
 */
public interface RetInService {

    public void save(RetInHis retIn, List<RetInHisDetail> listRetIn, List<String> delList);

    public void delete(String retInId);

    public List<RetInHis> search(String fromDate, String toDate, String cusId, String locId, String vouNo, String filterCode);

    public RetInHis findById(String id);
}
