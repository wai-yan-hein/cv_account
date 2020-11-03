/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.entity.RetOutDetailHis;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface RetOutDetailService {

    public RetOutDetailHis save(RetOutDetailHis pd);

    public List<RetOutDetailHis> search(String glId);

  //  public void save(PurHis gl, List<PurchaseDetail> pd,List<String> delList);

}
