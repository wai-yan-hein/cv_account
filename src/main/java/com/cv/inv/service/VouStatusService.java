/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.entity.VouStatus;
import java.util.List;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
public interface VouStatusService {

    public VouStatus save(VouStatus vouStatus);

    public List<VouStatus> findAll();

    public int delete(String id);

    public VouStatus findById(String id);
}
