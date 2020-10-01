/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.Region;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author WSwe
 */
@Repository
public class RegionDaoImpl extends AbstractDao<Integer, Region> implements RegionDao {

    @Override
    public Region save(Region region) {
        persist(region);
        return region;
    }

    @Override
    public Region findById(String id) {
        Region region = getByKey(Integer.parseInt(id));
        return region;
    }

    @Override
    public List<Region> search(String code, String name, String compCode,String parentCode) {
        String strSql = "select o from Region o ";
        String strFilter = "";

        if (!code.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.regionCode = '" + code + "'";
            } else {
                strFilter = strFilter + " and o.regionCode = '" + code + "'";
            }
        }

        if (!name.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.regionName like '%" + name + "%'";
            } else {
                strFilter = strFilter + " and o.regionName like '%" + name + "%'";
            }
        }

        if (!compCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.compId = '" + compCode + "'";
            } else {
                strFilter = strFilter + " and o.compId = '" + compCode + "'";
            }
        }
         if (!parentCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.parentRegion = '" + parentCode + "'";
            } else {
                strFilter = strFilter + " and o.parentRegion = '" + parentCode + "'";
            }
        }

        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }

        List<Region> listRegion = findHSQL(strSql);
        return listRegion;
    }

    @Override
    public int delete(String code, String compCode) {
        String strSql = "delete from Region o where o.regId = '"
                + code + "' and o.compId = " + compCode;
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }
}
