/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.ABank;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author WSwe
 */
@Repository
public class ABankDaoImpl extends AbstractDao<String, ABank> implements ABankDao{
    
    @Override
    public ABank save(ABank bank){
        persist(bank);
        return bank;
    }
    
    @Override
    public ABank findById(String id){
        ABank bank = getByKey(id);
        return bank;
    }
    
    @Override
    public List<ABank> search(String code, String name, String address, String phone,
            String compId){
        String strSql = "select o from ABank o ";
        String strFilter = "";
        
        if(!code.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.key.bankCode like '" + code + "%'";
            }else{
                strFilter = strFilter + " and o.key.bankCode like '" + code + "%'";
            }
        }
        
        if(!name.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.bankName like '%" + name + "%'";
            }else{
                strFilter = strFilter + " and o.bankName like '%" + name + "%'";
            }
        }
        
        if(!address.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.address like '%" + address + "%'";
            }else{
                strFilter = strFilter + " and o.address like '%" + address + "%'";
            }
        }
        
        if(!phone.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.phone like '%" + phone + "%'";
            }else{
                strFilter = strFilter + " and o.phone like '%" + phone + "%'";
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
        
        List<ABank> listBank = findHSQL(strSql);
        return listBank;
    }
    
    @Override
    public int delete(String code, String compId){
        String strSql = "delete from ABank o where o.key.bankCode = '" + code 
                + "' and o.key.compId = " + compId;
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }
}
