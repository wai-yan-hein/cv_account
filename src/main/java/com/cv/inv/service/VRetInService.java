/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.entity.view.VRetIn;
import java.util.List;

/**
 *
 * @author lenovo
 */
public interface VRetInService {

    public List<VRetIn> search(String fDate, String tDate, String cusId, String locId,
            String vouNo, String stockCodes, String compCode);

}
