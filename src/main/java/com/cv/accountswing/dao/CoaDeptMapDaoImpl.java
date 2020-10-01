/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.CoaDeptMap;
import com.cv.accountswing.entity.CoaDeptMapKey;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author winswe
 */
@Repository
public class CoaDeptMapDaoImpl extends AbstractDao<CoaDeptMapKey, CoaDeptMap> implements CoaDeptMapDao {

    @Override
    public CoaDeptMap save(CoaDeptMap cdm) {
        persist(cdm);
        return cdm;
    }

    @Override
    public CoaDeptMap findById(CoaDeptMapKey id) {
        CoaDeptMap cdm = getByKey(id);
        return cdm;
    }

    @Override
    public List<CoaDeptMap> search(String compId, String coaCode, String dept) {
        String strFilter = "";

        if (!compId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.key.compCode = " + compId;
            } else {
                strFilter = strFilter + " and o.key.compCode = " + compId;
            }
        }

        if (!coaCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.key.coaCode = '" + coaCode + "'";
            } else {
                strFilter = strFilter + " and o.key.coaCode = '" + coaCode + "'";
            }
        }

        if (!dept.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.key.deptCode = '" + dept + "'";
            } else {
                strFilter = strFilter + " and o.key.deptCode = '" + dept + "'";
            }
        }

        if (strFilter.isEmpty()) {
            strFilter = "select o from CoaDeptMap o";
        } else {
            strFilter = "select o from CoaDeptMap o where " + strFilter;
        }

        List<CoaDeptMap> listCDM = findHSQL(strFilter);
        return listCDM;
    }

    @Override
    public List searchMap(String compId, String coaCode, String dept) {
        String strFilter = "";

        if (strFilter.isEmpty()) {
            strFilter = "o.level >= 3";
        } else {
            strFilter = strFilter + " and o.level >= 3";
        }

        if (!compId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.compCode = " + compId;
            } else {
                strFilter = strFilter + " and o.compCode = " + compId;
            }
        }

        if (!coaCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.code = '" + coaCode + "'";
            } else {
                strFilter = strFilter + " and o.coaCode = '" + coaCode + "'";
            }
        }

        if (!dept.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.deptCode = '" + dept + "'";
            } else {
                strFilter = strFilter + " and o.deptCode = '" + dept + "'";
            }
        }

        if (strFilter.isEmpty()) {
            strFilter = "select o from VCOADeptMap o";
        } else {
            strFilter = "select o from VCOADeptMap o where " + strFilter;
        }

        List listCDM = findHSQL(strFilter);
        return listCDM;
    }

    @Override
    public int delete(String compId, String coaCode, String dept) {
        String strSql = "delete from CoaDeptMap o";
        String strFilter = "";
        int cnt = 0;

        if (!compId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.key.compCode = " + compId;
            } else {
                strFilter = strFilter + " and o.key.compCode = " + compId;
            }
        }

        if (!coaCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.key.coaCode = '" + coaCode + "'";
            } else {
                strFilter = strFilter + " and o.key.coaCode = '" + coaCode + "'";
            }
        }

        if (!dept.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.key.deptCode = '" + dept + "'";
            } else {
                strFilter = strFilter + " and o.key.deptCode = '" + dept + "'";
            }
        }

        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
            cnt = execUpdateOrDelete(strSql);
        }

        return cnt;
    }
}
