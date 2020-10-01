/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.inv.entity.RetInDetailHis;
import java.util.List;

/**
 *
 * @author lenovo
 */
public interface RetInDao {
    public RetInDetailHis save(RetInDetailHis retInDetailHis);
    public void delete (String retInId);
    public List search(String fDate,String tDate,String cusId,String locId,
            String vouNo,String stockCodes,String splitId,String tranSource,String compCode);
    
    
}
