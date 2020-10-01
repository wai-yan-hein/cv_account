/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.view.VCrDrVoucher;
import com.cv.accountswing.util.Util1;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author winswe
 */
@Repository
public class VCrDrVoucherDaoImpl extends AbstractDao<Long, VCrDrVoucher> implements VCrDrVoucherDao{
    
    @Override
    public List<VCrDrVoucher> search(String from, String to, String sourceAccId,
            String frmCurr, String dept, String vouNo, String compCode, String deptName,
            String splitId, String fromDesp, String naration, String remark, String toDesp){
        String strSql = "select o from VCrDrVoucher o ";
        String strFilter = "";
        
        if(!from.equals("-") && !to.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.glDate between '" + Util1.toDateStrMYSQL(from, "dd/MM/yyyy") + 
                        "' and '" + Util1.toDateStrMYSQL(to, "dd/MM/yyyy") + "'";
            }else{
                strFilter = strFilter + " and o.glDate between '" + 
                        Util1.toDateStrMYSQL(from, "dd/MM/yyyy") + "' and '" + Util1.toDateStrMYSQL(to, "dd/MM/yyyy") + "'";
            }
        }else if(!from.endsWith("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.glDate >= '" + Util1.toDateStrMYSQL(from, "dd/MM/yyyy") + "'";
            }else{
                strFilter = strFilter + " and o.glDate >= '" + Util1.toDateStrMYSQL(from, "dd/MM/yyyy") + "'";
            }
        }else if(!to.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.glDate <= '" + Util1.toDateStrMYSQL(to, "dd/MM/yyyy") + "'";
            }else{
                strFilter = strFilter + " and o.glDate <= '" + Util1.toDateStrMYSQL(to, "dd/MM/yyyy") + "'";
            }
        }
        
        if(!sourceAccId.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "(o.sourceAccId = '" + sourceAccId + "')";
            }else{
                strFilter = strFilter + " and (o.sourceAccId = '" + sourceAccId + "')";
            }
        }
        
        if(!frmCurr.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.currency = '" + frmCurr + "'";
            }else{
                strFilter = strFilter + " and o.currency = '" + frmCurr + "'";
            }
        }
        
        if(!dept.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.deptId = '" + dept + "'";
            }else{
                strFilter = strFilter + " and o.deptId = '" + dept + "'";
            }
        }
        
        if(!vouNo.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.voucherNo like '%" + vouNo + "%'";
            }else{
                strFilter = strFilter + " and o.voucherNo like '%" + vouNo + "%'";
            }
        }
        
        if(!compCode.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.compCode = " + compCode;
            }else{
                strFilter = strFilter + " and o.compCode = " + compCode;
            }
        }
        
        if(!deptName.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.deptName like '%" + deptName + "%'";
            }else{
                strFilter = strFilter + " and o.deptName like '%" + deptName + "%'";
            }
        }
        
        if(!splitId.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.splitId = " + splitId;
            }else{
                strFilter = strFilter + " and o.splitId = " + splitId;
            }
        }
        
        if(!fromDesp.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.fromDesp = '" + fromDesp + "'";
            }else{
                strFilter = strFilter + " and o.fromDesp = '" + fromDesp + "'";
            }
        }
        
        if(!naration.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.naration = '" + naration + "'";
            }else{
                strFilter = strFilter + " and o.naration = '" + naration + "'";
            }
        }
        
        if(!remark.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.remark = '" + remark + "'";
            }else{
                strFilter = strFilter + " and o.remark = '" + remark + "'";
            }
        }
        
        if(!toDesp.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.toDesp = '" + toDesp + "'";
            }else{
                strFilter = strFilter + " and o.toDesp = '" + toDesp + "'";
            }
        }
        
        if(!strFilter.isEmpty()){
            strSql = strSql + " where " + strFilter + " order by o.glDate, o.voucherNo";
        }
        
        List<VCrDrVoucher> listVCDV = findHSQL(strSql);
        return listVCDV;
    }
}
