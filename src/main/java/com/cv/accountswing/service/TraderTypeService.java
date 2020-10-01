/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.entity.TraderType;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface TraderTypeService {

    public TraderType save(TraderType trader);

    public List<TraderType> findAll();

    public int delete(String id);

    public TraderType findById(String id);
}
