/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.Region;
import java.util.List;

/**
 *
 * @author WSwe
 */
public interface RegionDao {
    public Region save(Region region);
    public Region findById(String id);
    public List<Region> search(String code, String name, String compCode,String parentCode);
    public int delete(String code, String compCode);
}
