/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.TraderOpeningH;
import com.cv.accountswing.util.Util1;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author winswe
 */
@Repository
public class TraderOpeningDaoImpl extends AbstractDao<Long, TraderOpeningH> implements TraderOpeningDao{
    
    @Override
    public TraderOpeningH save(TraderOpeningH toh){
        persist(toh);
        return toh;
    }
    
    @Override
    public TraderOpeningH findById(Long id){
        TraderOpeningH toh = getByKey(id);
        return toh;
    }
    
    @Override
    public List<TraderOpeningH> search(String from, String to, String compCode,
            String remark){
        String strSql = "select o from TraderOpeningH o ";
        String strFilter = "";
        
        if(!from.equals("-") && !to.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.opDate between '" + Util1.toDateStr(from, "dd/MM/yyyy", "yyyy-MM-dd") + 
                        "' and '" + Util1.toDateStr(to, "dd/MM/yyyy", "yyyy-MM-dd") + "'";
            }else{
                strFilter = strFilter + " and o.opDate between '" + 
                        Util1.toDateStr(from, "dd/MM/yyyy", "yyyy-MM-dd") + "' and '" + 
                        Util1.toDateStr(to, "dd/MM/yyyy", "yyyy-MM-dd") + "'";
            }
        }else if(!from.endsWith("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.opDate >= '" + Util1.toDateStr(from, "dd/MM/yyyy", "yyyy-MM-dd") + "'";
            }else{
                strFilter = strFilter + " and o.opDate >= '" + 
                        Util1.toDateStr(from, "dd/MM/yyyy", "yyyy-MM-dd") + "'";
            }
        }else if(!to.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.opDate <= '" + Util1.toDateStr(to, "dd/MM/yyyy", "yyyy-MM-dd") + "'";
            }else{
                strFilter = strFilter + " and o.opDate <= '" + 
                        Util1.toDateStr(to, "dd/MM/yyyy", "yyyy-MM-dd") + "'";
            }
        }
        
        if(!compCode.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.compCode = '" + compCode + "'";
            }else{
                strFilter = " and o.compCode = '" + compCode + "'";
            }
        }
        
        if(!remark.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.remark = '" + remark + "'";
            }else{
                strFilter = " and o.remark = '" + remark + "'";
            }
        }
        
        if(!strFilter.isEmpty()){
            strSql = strSql + " where " + strFilter;
        }
        
        List<TraderOpeningH> listTOH = findHSQL(strSql);
        return listTOH;
    }
    
    @Override
    public void generateZero(String tranIdH, String compCode, String currCode)throws Exception{
        String strSql = "insert into trader_opening_d(tran_id_h, trader_id, curr_code, op_amt) " +
                        "select " + tranIdH + ", trader_id, cur_code, 0 " +
                        "from v_trader_curr where comp_code = '" + compCode + 
                        "' and cur_code = '" + currCode + "'";
        execSQL(strSql);
    }
}
