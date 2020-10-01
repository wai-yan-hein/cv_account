/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.entity.RegionType;
import java.util.List;

/**
 *
 * @author WSwe
 */
public interface RegionTypeService {
    public RegionType save(RegionType regType);
    public RegionType findById(String id);
    public List<RegionType> search(String code, String name);
    public int delete(String code);
}
