/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.entity.PurHis;
import com.cv.inv.entity.PurchaseDetail;
import com.cv.inv.entity.RetInDetailHis;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface RetInDetailService {

    public RetInDetailHis save(RetInDetailHis pd);

    public List<RetInDetailHis> search(String glId);

  //  public void save(PurHis gl, List<PurchaseDetail> pd,List<String> delList);

}
