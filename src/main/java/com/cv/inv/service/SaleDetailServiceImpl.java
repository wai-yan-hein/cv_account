/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.dao.SaleDetailDao;
import com.cv.inv.entity.SaleDetailHis;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Service
@Transactional
public class SaleDetailServiceImpl implements SaleDetailService {

    private SaleDetailDao dao;

    @Override
    public SaleDetailHis save(SaleDetailHis sdh) {
        return dao.save(sdh);
    }

    @Override
    public List<SaleDetailHis> search(String glId) {
        return dao.search(glId);
    }

}
