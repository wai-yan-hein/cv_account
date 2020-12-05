/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cv.inv.dao.SReportDao;
import java.util.Map;

/**
 *
 * @author Lenovo
 */
@Service
@Transactional
public class SReportServiceImpl implements SReportService {

    @Autowired
    private SReportDao dao;

    @Override
    public void generateStockBalance(String stockCode, String stockTypeCode, String brandId, String catId, String locId, String changeUnit) {
        dao.generateStockBalance(stockCode, stockTypeCode, brandId, catId, locId, changeUnit);
    }

    @Override
    public void reportViewer(String reportPath, String filePath, String fontPath, Map<String, Object> parameters) {
        dao.reportViewer(reportPath, filePath, fontPath, parameters);
    }

   

}
