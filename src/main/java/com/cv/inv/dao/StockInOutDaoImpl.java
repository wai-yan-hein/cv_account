/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.accountswing.dao.AbstractDao;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.StockInOut;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class StockInOutDaoImpl extends AbstractDao<Integer, StockInOut> implements StockInOutDao {

    @Override
    public StockInOut save(StockInOut stock) {
        persist(stock);
        return stock;
    }

    @Override
    public List<StockInOut> search(String fromDate, String toDate, String stockCode, String locId, String option, String remark) {
        String hsql = "select o from  StockInOut o";
        String hFilter = "";
        if (!fromDate.equals("-") && !toDate.equals("-")) {
            if (hFilter.isEmpty()) {
                hFilter = "date(o.date) between '" + Util1.toDateStrMYSQL(fromDate)
                        + "' and '" + Util1.toDateStrMYSQL(toDate) + "'";
            } else {
                hFilter = hFilter + " and date(o.date) between '"
                        + Util1.toDateStrMYSQL(fromDate) + "' and '" + Util1.toDateStrMYSQL(toDate) + "'";
            }
        } else if (!fromDate.endsWith("-")) {
            if (hFilter.isEmpty()) {
                hFilter = "date(o.date) >= '" + Util1.toDateStrMYSQL(fromDate) + "'";
            } else {
                hFilter = hFilter + " and date(o.date) >= '" + Util1.toDateStrMYSQL(fromDate) + "'";
            }
        } else if (!toDate.equals("-")) {
            if (hFilter.isEmpty()) {
                hFilter = "date(o.date) <= '" + Util1.toDateStrMYSQL(toDate) + "'";
            } else {
                hFilter = hFilter + " and date(o.date) <= '" + Util1.toDateStrMYSQL(toDate) + "'";
            }
        }
        if (!stockCode.equals("-")) {
            if (hFilter.isEmpty()) {
                hFilter = "o.stock.stockCode = '" + stockCode + "'";
            } else {
                hFilter = hFilter + " and o.stock.stockCode = '" + stockCode + "'";
            }
        }
        if (!locId.equals("-")) {
            if (hFilter.isEmpty()) {
                hFilter = "o.location.locId = '" + locId + "'";
            } else {
                hFilter = hFilter + " and o.location.locId = '" + locId + "'";
            }
        }
        if (!option.equals("-")) {
            if (hFilter.isEmpty()) {
                hFilter = "o.optionType = '" + option + "'";
            } else {
                hFilter = hFilter + " and o.optionType = '" + option + "'";
            }
        }
        if (!remark.equals("-")) {
            if (hFilter.isEmpty()) {
                hFilter = "o.remark = '" + remark + "'";
            } else {
                hFilter = hFilter + " and o.remark = '" + remark + "'";
            }
        }
        if (!hFilter.isEmpty()) {
            hsql = hsql + " where " + hFilter;
        }
        return findHSQL(hsql);
    }

    @Override
    public int delete(Integer id) {
        String hsql = "delete from StockInOut o where o.id = '" + id.toString() + "'";
        return execUpdateOrDelete(hsql);
    }

}
