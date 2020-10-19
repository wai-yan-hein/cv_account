/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.Department;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author WSwe
 */
@Repository
public class DepartmentDaoImpl extends AbstractDao<String, Department> implements DepartmentDao {

    @Override
    public Department save(Department dept) {
        persist(dept);
        return dept;
    }

    @Override
    public Department findById(String id) {
        Department dept = getByKey(id);
        return dept;
    }

    @Override
    public List<Department> search(String code, String name, String compCode,
            String usrCode, String parentId) {
        String strSql = "select o from Department o ";
        String strFilter = "";

        if (!code.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.deptCode like '" + code + "%'";
            } else {
                strFilter = strFilter + " and o.deptCode like '" + code + "%'";
            }
        }

        if (!name.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.deptName like '%" + name + "%'";
            } else {
                strFilter = strFilter + " and o.deptName like '%" + name + "%'";
            }
        }

        if (!compCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.compCode = " + compCode;
            } else {
                strFilter = strFilter + " and o.compCode = " + compCode;
            }
        }

        if (!usrCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.usrCode like '" + usrCode + "%'";
            } else {
                strFilter = strFilter + " and o.usrCode like '" + usrCode + "%'";
            }
        }
        if (!parentId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.parentDept like '" + parentId + "%'";
            } else {
                strFilter = strFilter + " and o.parentDept like '" + parentId + "%'";
            }
        }

        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }

        strSql = strSql + " order by o.deptName";

        List<Department> listBank = findHSQL(strSql);
        return listBank;
    }

    @Override
    public int delete(String code) {
        String strSql = "delete from Department o where o.deptCode = '" + code + "'";
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }

    @Override
    public List<Department> findAll() {
        String hsql = "select o from Department o";
        return findHSQL(hsql);
    }
}
