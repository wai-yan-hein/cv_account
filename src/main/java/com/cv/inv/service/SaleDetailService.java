/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.accountswing.entity.Gl;
import com.cv.inv.entity.SaleDetailHis;
import java.util.List;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
public interface SaleDetailService {

    public SaleDetailHis save(SaleDetailHis sdh);

    public List<SaleDetailHis> search(String glId);

    public void save(Gl gl, List<SaleDetailHis> listSaleDetail);
}
