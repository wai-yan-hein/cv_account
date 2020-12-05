/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.entity.PurHis;
import com.cv.inv.entity.PurHisDetail;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface PurchaseDetailService {

    public PurHisDetail save(PurHisDetail pd);

    public List<PurHisDetail> search(String glId);

    public void save(PurHis gl, List<PurHisDetail> pd,List<String> delList);

}
