/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.accountswing.dao.AbstractDao;
import com.cv.inv.entity.Location;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class LocationDaoImpl extends AbstractDao<Integer, Location> implements LocationDao {

    @Override
    public Location save(Location ch) {
        persist(ch);
        return ch;
    }

    @Override
    public List<Location> findAll() {
        String hsql = "select o from Location o";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        String hsql = "delete from Location o where o.locationId='" + id + "'";
        return execUpdateOrDelete(hsql);
    }

    @Override
    public Location findById(String id) {
        return getByKey(Integer.parseInt(id));
    }

    @Override
    public List<Location> search(String parent) {
        String hsql = "select o from Location o where o.parent ='" + parent + "'";
        return findHSQL(hsql);

    }

}
