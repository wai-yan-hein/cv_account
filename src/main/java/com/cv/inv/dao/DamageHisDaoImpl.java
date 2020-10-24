/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.accountswing.dao.AbstractDao;
import com.cv.inv.entity.DamageHis;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author lenovo
 */
@Repository
public class DamageHisDaoImpl extends AbstractDao<String, DamageHis> implements DamageHisDao {

    @Autowired
    private DamageDetailHisDao detaildao;

    @Override
    public DamageHis save(DamageHis ph) {
        persist(ph);
        return ph;
    }

    @Override
    public DamageHis findById(String id) {
        DamageHis ph = getByKey(id);
        return ph;
    }

    @Override
    public List<DamageHis> search(String from, String to, String location, String remark, String vouNo) {
        String strFilter = "";

        if (!from.equals("-") && !to.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "v.dmgDate between '" + from
                        + "' and '" + to + "'";
            } else {
                strFilter = strFilter + " and v.dmgDate between '" + from
                        + "' and '" + to + "'";
            }
        } else if (!from.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "v.dmgDate >= '" + from + "'";
            } else {
                strFilter = strFilter + " and v.dmgDate >= '" + from + "'";
            }
        } else if (!to.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "v.dmgDate <= '" + to + "'";
            } else {
                strFilter = strFilter + " and v.dmgDate <= '" + to + "'";
            }
        }

        if (!location.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "v.location = " + location;
            } else {
                strFilter = strFilter + " and v.location = " + location;
            }
        }

        if (!remark.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "v.remark like '%" + remark + "%'";
            } else {
                strFilter = strFilter + " like v.remark '%" + remark + "%'";
            }
        }

        if (!vouNo.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "v.dmgVouId like '%" + vouNo + "%'";
            } else {
                strFilter = strFilter + " like v.dmgVouId '%" + vouNo + "%'";
            }
        }

        String strSql = "select distinct v from DamageHis v";

        List<DamageHis> listDH = null;
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
            listDH = findHSQL(strSql);
        }

        return listDH;
    }

    @Override
    public int delete(String vouNo) {
        String strSql1 = "delete from DamageDetailHis o where o.dmgVouId = '" + vouNo + "'";
        execUpdateOrDelete(strSql1);
        String strSql = "delete from DamageHis o where o.dmgVouId = '" + vouNo + "'";
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }

}
