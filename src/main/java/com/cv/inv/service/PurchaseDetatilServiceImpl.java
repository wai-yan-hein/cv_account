/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.dao.PurchaseDetailDao;
import com.cv.inv.entity.PurchaseDetail;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Lenovo
 */
@Service
@Transactional
public class PurchaseDetatilServiceImpl implements PurchaseDetailService {

    private PurchaseDetailDao dao;

    @Override
    public PurchaseDetail save(PurchaseDetail pd) {
        return dao.save(pd);
    }

    @Override
    public List<PurchaseDetail> search(String glId) {
        return dao.search(glId);
    }

}
