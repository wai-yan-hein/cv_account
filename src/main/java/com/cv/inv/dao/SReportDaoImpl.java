/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.dao.AbstractDao;
import java.io.Serializable;
import java.util.Map;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */


@Repository
public class SReportDaoImpl extends AbstractDao<Serializable, Object> implements SReportDao {
    
    private static final Logger log = LoggerFactory.getLogger(SReportDaoImpl.class);
    
    @Override
    public void generateStockBalance(String stockCode, String stockTypeCode, String brandId, String catId, String locId, String changeUnit) {
        try {
            deleteTmpFilter();
            insertTmpStockFilter(stockCode, locId, changeUnit);
            calculateBalance();
        } catch (Exception ex) {
            log.error("calclate stock balance :" + ex.getMessage());
        }
    }
    
    private void insertTmpStockFilter(String stockCode, String locId, String reqUnit) throws Exception {
        String filterSql = "insert tmp_stock_filter(stock_code, comp_id, loc_id, machine_id)\n"
                + "select stock_code," + Global.compId + ",location_id," + Global.machineId + "\n"
                + "from v_stock_loc";
        String andSql = "";
        if (!stockCode.equals("-")) {
            if (andSql.isEmpty()) {
                andSql = "stock_code in  (" + stockCode + ")";
            } else {
                andSql = andSql + " and stock_code '" + stockCode + "'";
            }
        }
        if (!locId.equals("-")) {
            if (andSql.isEmpty()) {
                andSql = "location_id  = '" + locId + "'";
            } else {
                andSql = andSql + " and location_id = '" + locId + "'";
            }
        }
        if (!andSql.isEmpty()) {
            filterSql = filterSql + " where " + andSql;
        }
        execSQL(filterSql);
        log.info("insert tmp table success.");
    }
    
    private void calculateBalance() throws Exception {
        String insertSql = "insert into tmp_stock_balance(stock_code,qty,std_wt,std_wt_ttl,small_wt_ttl,small_unit,loc_id,machine_id)\n"
                + "select b.stock_code,sum(b.qty) qty,b.std_wt as std_wt,sum(b.std_wt_ttl) as std_wt_ttl,sum(b.small_wt) as small_wt_ttl,b.small_unit,b.loc," + Global.machineId + "\n"
                + "	from(\n"
                + "		select p.stock_code,sum(p.qty) as qty,p.std_wt ,p.std_small_wt as std_wt_ttl,p.small_wt,p.small_unit,p.loc_id as loc \n"
                + "		from v_purchase p,tmp_stock_filter tmp\n"
                + "        where p.stock_code = tmp.stock_code and p.loc_id = tmp.loc_id and p.comp_code = tmp.comp_id\n"
                + "		group by stock_code,loc,std_wt\n"
                + "			union all\n"
                + "		select s.stock_code,sum(s.qty)*-1 as qty,s.std_weight as std_wt,(s.std_small_wt)*-1 as std_wt_ttl,(s.small_wt)*-1 small_wt,s.small_unit,s.loc_id as loc \n"
                + "		from v_sale s, tmp_stock_filter tmp\n"
                + "		where s.stock_code = tmp.stock_code and s.loc_id = tmp.loc_id and s.comp_code = tmp.comp_id\n"
                + "		group by stock_code,loc,std_weight\n"
                + "        ) b\n"
                + "group by b.stock_code,b.loc";
        execSQL(insertSql);
        log.info("insert stock balance tmp success.");
    }
    
    private void deleteTmpFilter() throws Exception {
        String delSql = "delete from tmp_stock_filter where machine_id = " + Global.machineId + "";
        String delSql1 = "delete from tmp_stock_balance where machine_id = " + Global.machineId + "";
        execSQL(delSql, delSql1);
        log.info("delete tmp table success.");
    }
    
    @Override
    public void reportViewer(String reportPath, String filePath, String fontPath, Map<String, Object> parameters) {
        try {
            doReportPDF(reportPath, filePath, parameters, fontPath);
        } catch (Exception ex) {
            log.error("Report Viewer Error :" + ex.getMessage());
        }
    }
    
}
