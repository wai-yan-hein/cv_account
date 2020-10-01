/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.accountswing.dao.AbstractDao;
import com.cv.inv.entity.Category;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class CategoryDaoImpl extends AbstractDao<String, Category> implements CategoryDao {

    @Override
    public Category save(Category item) {
        persist(item);
        return item;
    }

    @Override
    public List<Category> findAll() {
        String hsql = "select o from Category o";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        String hsql = "delete from Category o where o.catId='" + id + "'";
        return execUpdateOrDelete(hsql);
    }

}
