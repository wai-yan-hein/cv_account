/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.AutoText;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class AutoTextDaoImpl extends AbstractDao<String, AutoText> implements AutoTextDao {

    @Override
    public AutoText save(AutoText autoText) {
        persist(autoText);
        return autoText;
    }

    @Override
    public List<AutoText> search(String option) {
        String strSql = "select o from AutoText o ";
        String strFilter = "";

        if (!option.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.option = '" + option + "'";
            } else {
                strFilter = strFilter + " and o.option = '" + option + "'";
            }
        }
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }
        List<AutoText> listAutoText = findHSQL(strSql);
        return listAutoText;
    }

}
