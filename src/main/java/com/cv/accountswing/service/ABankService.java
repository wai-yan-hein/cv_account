/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.entity.ABank;
import java.util.List;

/**
 *
 * @author winswe
 */
public interface ABankService {
    public ABank save(ABank bank);
    public ABank findById(String id);
    public List<ABank> search(String code, String name, String address, String phone,
            String compId);
    public int delete(String code, String compId);
}
