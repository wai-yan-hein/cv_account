/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.inv.entity.Category;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface CategoryDao {

    public Category save(Category item);

    public List<Category> findAll();

    public int delete(String id);
}
