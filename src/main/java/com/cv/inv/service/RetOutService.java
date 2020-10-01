/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.accountswing.entity.Gl;
import com.cv.inv.entity.RetOutDetailHis;
import java.util.List;

/**
 *
 * @author lenovo
 */
public interface RetOutService {
    public RetOutDetailHis save(Gl saveGl,List<RetOutDetailHis> listRetOut);

    public void delete(String retOutId);
    
}
