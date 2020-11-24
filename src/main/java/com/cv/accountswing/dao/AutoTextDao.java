/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.AutoText;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface AutoTextDao {

    public AutoText save(AutoText autoText);

    public List<AutoText> search(String option);
}
