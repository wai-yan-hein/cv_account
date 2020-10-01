/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.ABank;
import java.util.List;

/**
 *
 * @author WSwe
 */
public interface ABankDao {
    public ABank save(ABank bank);
    public ABank findById(String id);
    public List<ABank> search(String code, String name, String address, String phone,
            String compId);
    public int delete(String code, String compId);
}
