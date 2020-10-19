/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.entity.DamageDetailHis;
import java.util.List;

/**
 *
 * @author lenovo
 */
public interface DamageDetailHisService {
     public List<DamageDetailHis> search(String dmgVouId);
}
