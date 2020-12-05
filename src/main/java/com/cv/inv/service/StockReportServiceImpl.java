/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.dao.StockReportDao;
import com.cv.inv.entity.StockReport;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Lenovo
 */
@Service
@Transactional
public class StockReportServiceImpl implements StockReportService {

    @Autowired
    private StockReportDao dao;

    @Override
    public StockReport save(StockReport report) {
        return dao.save(report);
    }

    @Override
    public List<StockReport> getReports() {
        return dao.getReports();
    }

}
