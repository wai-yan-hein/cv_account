/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.entity.SaleDetailHis;
import com.cv.inv.entity.SaleHis;
import java.util.List;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
public interface SaleDetailService {

    public SaleDetailHis save(SaleDetailHis sdh);

    public List<SaleDetailHis> search(String vouId);

    public void save(SaleHis saleHis, List<SaleDetailHis> listSaleDetail,
            String vouStatus, List<String> deleteList) throws Exception;
}
