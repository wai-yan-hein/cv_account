/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.entity.CharacterNo;
import com.cv.inv.entity.StockType;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface CharacterNoService {

    public CharacterNo save(CharacterNo ch);

    public List<CharacterNo> findAll();

    public int delete(String id);

    public CharacterNo findById(String id);
}
