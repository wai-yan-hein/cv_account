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
 * @author Lenovo
 */
public interface RetInDetailDao {

    public RetInDetailHis save(RetInDetailHis pd);

    public List<RetInDetailHis> search(String glId);

    public int delete(String id);

}
