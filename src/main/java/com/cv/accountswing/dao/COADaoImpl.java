/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.COALevel;
import com.cv.accountswing.entity.ChartOfAccount;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author winswe
 */
@Repository
public class COADaoImpl extends AbstractDao<String, ChartOfAccount> implements COADao {

    @Override
    public ChartOfAccount save(ChartOfAccount coa) {
        persist(coa);
        return coa;
    }

    @Override
    public ChartOfAccount findById(String id) {
        ChartOfAccount coa = getByKey(id);
        return coa;
    }

    @Override
    public List<ChartOfAccount> search(String code, String name, String compCode,
            String level, String parent, String userParent, String usrCoaCode) {
        String strSql = "select o from ChartOfAccount o ";
        String strFilter = "";

        if (!code.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.code like '" + code + "%'";
            } else {
                strFilter = strFilter + " and o.code like '" + code + "%'";
            }
        }

        if (!name.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.coaNameEng like '%" + name + "%'";
            } else {
                strFilter = strFilter + " and o.coaNameEng like '%" + name + "%'";
            }
        }

        if (!compCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.compCode = '" + compCode + "'";
            } else {
                strFilter = strFilter + " and o.compCode = '" + compCode + "'";
            }
        }

        if (!level.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.level = " + level;
            } else {
                strFilter = strFilter + " and o.level = " + level;
            }
        }

        if (!parent.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.parent = '" + parent + "'";
            } else {
                strFilter = strFilter + " and o.parent = '" + parent + "'";
            }
        }

        if (!userParent.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.parentUsrCode like '" + userParent + "%'";
            } else {
                strFilter = strFilter + " and o.parentUsrCode like '" + userParent + "%'";
            }
        }

        if (!usrCoaCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.coaCodeUsr = '" + usrCoaCode + "'";
            } else {
                strFilter = strFilter + " and o.coaCodeUsr = '" + usrCoaCode + "'";
            }
        }

        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }

        strSql = strSql + " order by o.coaCodeUsr";

        List<ChartOfAccount> listCOA = findHSQL(strSql);
        return listCOA;
    }

    @Override
    public int delete(String code, String compCode) {
        String strSql = "delete from ChartOfAccount o where o.code = '"
                + code + "' and o.compCode = '" + compCode + "'";
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }

    @Override
    public List<ChartOfAccount> getParent(String compCode) {
        String strSql = "select o from ChartOfAccount o where o.option = 'SYS'";
        List<ChartOfAccount> listCOA = findHSQL(strSql);
        return listCOA;
    }

    @Override
    public List<COALevel> getParentChildCOA(String compCode) {
        String strSql = "select o from COALevel o ";
        String strFilter = "";

        if (!compCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.compCode is null";
            } else {
                strFilter = strFilter + " and o.compCode is null";
            }
        }

        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }

        List<COALevel> listCOAL = findHSQLPC(strSql, "compFilter", "companyParam",
                compCode);
        return listCOAL;
    }

    @Override
    public List<ChartOfAccount> getCOALevel3Above(String compCode) {
        String strSql = "select o from ChartOfAccount o where o.compCode = '"
                + compCode + "' and o.level >= 3 order by o.level, o.coaNameEng";
        List<ChartOfAccount> listCOA = findHSQL(strSql);
        return listCOA;
    }

    @Override
    public List<ChartOfAccount> getCOALevel2Above(String compCode) {
        String strSql = "select o from ChartOfAccount o where o.compCode = '"
                + compCode + "' and o.level >= 2 order by o.level, o.coaNameEng";
        List<ChartOfAccount> listCOA = findHSQL(strSql);
        return listCOA;
    }

    @Override
    public List<ChartOfAccount> getAllChild(String parent, String compCode) {
        String strSql = "select o from ChartOfAccount o where o.compCode = '"
                + compCode + "' and o.code = '" + parent + "'";
        List<ChartOfAccount> listAllChild = findHSQL(strSql);
        if (listAllChild == null) {
            listAllChild = new ArrayList();
        }
        getChild(listAllChild, parent, compCode);
        return listAllChild;
    }

    private void getChild(List<ChartOfAccount> listAllChild, String parent, String compCode) {
        String strSql = "select o from ChartOfAccount o where o.compCode = '"
                + compCode + "' and o.parent = '" + parent + "'";
        List<ChartOfAccount> listCOA = findHSQL(strSql);

        if (listCOA != null) {
            if (!listCOA.isEmpty()) {
                listAllChild.addAll(listCOA);
            }
        }

        for (ChartOfAccount coa : listCOA) {
            getChild(listAllChild, coa.getCode(), compCode);
        }
    }

    @Override
    public List<ChartOfAccount> getChild(String compCode, String parent) {
        String strSql = "select o from ChartOfAccount o where o.compCode = '"
                + compCode + "' and o.parent = '" + parent + "'";
        List<ChartOfAccount> listCOA = findHSQL(strSql);
        return listCOA;
    }

    @Override
    public List<ChartOfAccount> getCOAWithLevel(String compCode, String level) {
        String strSql = "select o from ChartOfAccount o where o.compCode = '"
                + compCode + "' and o.level = " + level;
        List<ChartOfAccount> listCOA = findHSQL(strSql);
        return listCOA;
    }

    @Override
    public List<ChartOfAccount> getCompanyCOA(String compCode) {
        String strSql = "select o from ChartOfAccount o where o.compCode = '"
                + compCode + "' or o.parent = '#' order by o.level, o.coaNameEng";
        List<ChartOfAccount> listCOA = findHSQL(strSql);
        return listCOA;
    }

    @Override
    public List<ChartOfAccount> getCompanyCOA(String compCode, String deptId, String projectId) {
        String strSql = "select o from ChartOfAccount o where (o.compCode = '"
                + compCode + "' or o.parent = '#') and o.code in "
                + "(select i.coaCode from ProjectCOAMapping i where i.projectId = "
                + projectId + ") order by o.level, o.coaNameEng";
        List<ChartOfAccount> listCOA = findHSQL(strSql);

        if (listCOA.isEmpty()) {
            strSql = "select o from ChartOfAccount o where (o.compCode = '"
                    + compCode + "' or o.parent = '#') and o.code in (select i.key.coaCode "
                    + "from CoaDeptMap i where i.key.deptCode = '" + deptId + "') order by o.level, o.coaNameEng";
            listCOA = findHSQL(strSql);
        }

        if (listCOA.isEmpty()) {
            strSql = "select o from ChartOfAccount o where o.compCode = '"
                    + compCode + "' or o.parent = '#' order by o.level, o.coaNameEng";
            listCOA = findHSQL(strSql);
        }

        return listCOA;
    }
}
