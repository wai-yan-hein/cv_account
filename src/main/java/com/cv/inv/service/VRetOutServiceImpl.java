/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.dao.VRetOutDao;
import com.cv.inv.entity.view.VRetOut;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author lenovo
 */
@Service
@Transactional
public class VRetOutServiceImpl implements VRetOutService {

    @Autowired
    private VRetOutDao dao;

    @Override
    public List<VRetOut> search(String fDate, String tDate, String cusId, String locId, String vouNo, String stockCodes, String compCode) {
        return dao.search(fDate, tDate, cusId, locId, vouNo, stockCodes, compCode);
    }

}
