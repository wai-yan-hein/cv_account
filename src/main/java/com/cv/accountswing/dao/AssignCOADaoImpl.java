/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.AssignCOA;
import com.cv.accountswing.entity.AssignCOAKey;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author winswe
 */
@Repository
public class AssignCOADaoImpl extends AbstractDao<AssignCOAKey, AssignCOA> implements AssignCOADao{
    
    @Override
    public AssignCOA save(AssignCOA acoa){
        persist(acoa);
        return acoa;
    }
    
    @Override
    public AssignCOA findById(AssignCOAKey key){
        AssignCOA acoa = getByKey(key);
        return acoa;
    }
    
    @Override
    public List search(String compId, String roleId, String deptCode){
        String strFilter = "";
        
        if(!compId.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.key.compId = " + compId;
            }else{
                strFilter = strFilter + " and o.key.compId = " + compId;
            }
        }
        
        if(!roleId.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.key.roleId = " + roleId;
            }else{
                strFilter = strFilter + " and o.key.roleId = " + roleId;
            }
        }
        
        if(!deptCode.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.key.deptCode = '" + deptCode + "'";
            }else{
                strFilter = strFilter + " and o.key.deptCode = '" + deptCode + "'";
            }
        }
        
        List list = null;
        if(!strFilter.isEmpty()){
            strFilter = "select o from VAssignCOA o where " + strFilter;
            list = findHSQL(strFilter);
        }
        
        return list;
    }
    
    @Override
    public int delete(String compId, String roleId, String deptCode, String coaCode){
        int cnt = 0;
        String strFilter = "";
        
        if(!compId.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.key.compId = " + compId;
            }else{
                strFilter = strFilter + " and o.key.compId = " + compId;
            }
        }
        
        if(!roleId.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.roleId = " + roleId;
            }else{
                strFilter = strFilter + " and o.roleId = " + roleId;
            }
        }
        
        if(!deptCode.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.deptCode = '" + deptCode + "'";
            }else{
                strFilter = strFilter + " and o.deptCode = '" + deptCode + "'";
            }
        }
        
        if(!coaCode.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.coaCode = '" + coaCode + "'";
            }else{
                strFilter = strFilter + " and o.coaCode = '" + coaCode + "'";
            }
        }
        
        if(!strFilter.isEmpty()){
            strFilter = "delete from AssignCOA o where " + strFilter;
            cnt = execUpdateOrDelete(strFilter);
        }
        
        return cnt;
    }
    
    @Override
    public void updateNew(String compId, String roleId, String deptCode) throws Exception{
        String strSql = "insert into assign_coa(comp_id, role_id, dept_code, coa_code)\n" +
            "select comp_code," + roleId + ",'" + deptCode + "',coa_code\n" +
            "from chart_of_account \n" +
            "where comp_code = " + compId + " and level >= 3\n" +
            "and coa_code not in (select coa_code from assign_coa "
                + "where comp_id = " + compId + " and role_id = " + roleId 
                + " and dept_code = '" + deptCode + "')";
        execSQL(strSql);
    }
}
