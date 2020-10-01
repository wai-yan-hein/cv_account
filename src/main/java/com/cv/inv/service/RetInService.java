/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.accountswing.entity.Gl;
import com.cv.inv.entity.RetInDetailHis;
import java.util.List;

/**
 *
 * @author lenovo
 */
public interface RetInService {
    public RetInDetailHis save(Gl gl,List<RetInDetailHis> listRetIn);
    public void delete (String retInId);
    public List search(String fDate,String tDate,String cusId,String locId,
            String vouNo,String stockCodes,String splitId,String tranSource,String compCode);
    
}
