/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.accountswing.dao.AbstractDao;
import com.cv.inv.entity.StockReport;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class StockReportDaoImpl extends AbstractDao<Integer, StockReport> implements StockReportDao {

    @Override
    public StockReport save(StockReport report) {
        persist(report);
        return report;
    }

    @Override
    public List<StockReport> getReports() {
        String hsql = "select o from StockReport o";
        return findHSQL(hsql);
    }

}
