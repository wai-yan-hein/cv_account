/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.entity.view.VRetOut;
import java.util.List;

/**
 *
 * @author lenovo
 */
public interface VRetOutService {

    public List<VRetOut> search(String fDate, String tDate, String cusId, String locId,
            String vouNo, String stockCodes, String compCode);

}
