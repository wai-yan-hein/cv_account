/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.inv.service.*;
import com.cv.inv.entity.PurchaseDetail;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface PurchaseDetailDao {

    public PurchaseDetail save(PurchaseDetail pd);

    public List<PurchaseDetail> search(String glId);
    
}
