/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.inv.entity.SaleDetailHis;
import java.util.List;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
public interface SaleDetailDao {

    public SaleDetailHis save(SaleDetailHis sdh);

    public List<SaleDetailHis> search(String glId);
}
