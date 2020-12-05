/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.inv.entity.StockReport;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface StockReportDao {

    public StockReport save(StockReport report);

    public List<StockReport> getReports();
}
