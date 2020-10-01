/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.StockOpValue;
import com.cv.accountswing.entity.StockOpValueKey;
import com.cv.accountswing.util.Util1;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author winswe
 */
@Repository
public class StockOpValueDaoImpl extends AbstractDao<StockOpValueKey, StockOpValue> implements StockOpValueDao{
    
    @Override
    public StockOpValue save(StockOpValue value){
        persist(value);
        return value;
    }
    
    @Override
    public StockOpValue findById(StockOpValueKey key){
        StockOpValue value = getByKey(key);
        return value;
    }
    
    @Override
    public List search(String from, String to, String coaCode, String currency,
            String dept, String compId){
        String strSql = "select o from VStockOpValue o";
        String strFilter = "";
        
        if(!from.equals("-") && !to.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "date(o.key.tranDate) between '" + Util1.toDateStrMYSQL(from, "dd/MM/yyyy") + 
                        "' and '" + Util1.toDateStrMYSQL(to, "dd/MM/yyyy") + "'";
            }else{
                strFilter = strFilter + " and date(o.key.tranDate) between '" + 
                        Util1.toDateStrMYSQL(from, "dd/MM/yyyy") + "' and '" + Util1.toDateStrMYSQL(to, "dd/MM/yyyy") + "'";
            }
        }else if(!from.endsWith("-")){
            if(strFilter.isEmpty()){
                strFilter = "date(o.key.tranDate) >= '" + Util1.toDateStrMYSQL(from, "dd/MM/yyyy") + "'";
            }else{
                strFilter = strFilter + " and date(o.key.tranDate) >= '" + Util1.toDateStrMYSQL(from, "dd/MM/yyyy") + "'";
            }
        }else if(!to.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "date(o.key.tranDate) <= '" + Util1.toDateStrMYSQL(to, "dd/MM/yyyy") + "'";
            }else{
                strFilter = strFilter + " and date(o.key.tranDate) <= '" + Util1.toDateStrMYSQL(to, "dd/MM/yyyy") + "'";
            }
        }
        
        if(!coaCode.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.key.coaCode = '" + coaCode + "'";
            }else{
                strFilter = strFilter + " and o.key.coaCode = '" + coaCode + "'";
            }
        }
        
        if(!currency.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.key.currency = '" + currency + "'";
            }else{
                strFilter = strFilter + " and o.key.currency = '" + currency + "'";
            }
        }
        
        if(!dept.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.key.deptCode = '" + dept + "'";
            }else{
                strFilter = strFilter + " and o.key.deptCode = '" + dept + "'";
            }
        }
        
        if(!compId.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.key.compId = " + compId;
            }else{
                strFilter = strFilter + " and o.key.compId = " + compId;
            }
        }
        
        if(!strFilter.isEmpty()){
            strSql = strSql + " where " + strFilter;
        }
        
        List<StockOpValue> listValue = findHSQL(strSql + " order by o.key.tranDate");
        return listValue;
    }
    
    @Override
    public void backup(String tranDate, String coaCode, String dept, String currency,
            String compId, String userId, String option) throws Exception{
        String strSql = "insert into stock_op_value_log(tran_date, coa_code, dept_code,\n" +
        "  curr_code, comp_id, amount, remark, user_id, log_date, created_by, created_date, updated_by, updated_date, log_option)\n" +
        "select tran_date, coa_code, dept_code, curr_code, comp_id, amount, remark, '" + userId + "'" +
                ", '" + Util1.getTodayDateTimeStrMySql() 
                + "', created_by, created_date, updated_by, updated_date, '" + option + "' " +
        "from stock_op_value where date(tran_date) = '" + tranDate + "' and coa_code = '" + coaCode + 
        "' and dept_code = '" + dept + "' and curr_code = '" + currency + "' and comp_id = " + compId;
        execSQL(strSql);
    }
    
    @Override
    public int delete(String tranDate, String coaCode, String dept, String currency,
            String compId){
        String strSql = "delete from stock_op_value o where date(o.tran_date) = '" 
                + Util1.toDateStrMYSQL(tranDate) + "' and o.coaCode = '" + coaCode 
                + "' and o.deptCode = '" + dept + "' and o.currency = '" + currency
                + "' and o.compId = " + compId;
        
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }
}
