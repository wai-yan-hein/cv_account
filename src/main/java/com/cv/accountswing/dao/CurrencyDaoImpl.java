/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.Currency;
import com.cv.accountswing.entity.CurrencyKey;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author WSwe
 */
@Repository
public class CurrencyDaoImpl extends AbstractDao<CurrencyKey, Currency> implements CurrencyDao {

    @Override
    public Currency save(Currency cur) {
        persist(cur);
        return cur;
    }

    @Override
    public Currency findById(CurrencyKey id) {
        Currency cur = getByKey(id);
        return cur;
    }

    @Override
    public Currency findById(String id) {

        return null;
    }

    @Override
    public List<Currency> search(String code, String name, String compCode) {
        String strSql = "select o from Currency o ";
        String strFilter = "";

        if (!code.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.key.code like '" + code + "%'";
            } else {
                strFilter = strFilter + " and o.key.code like '" + code + "%'";
            }
        }

        if (!name.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.currencyName like '%" + name + "%'";
            } else {
                strFilter = strFilter + " and o.currencyName like '%" + name + "%'";
            }
        }

        if (!compCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.key.compCode = " + compCode;
            } else {
                strFilter = strFilter + " and o.key.compCode = " + compCode;
            }
        }

        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }

        List<Currency> listCurrency = findHSQL(strSql);
        return listCurrency;
    }

    @Override
    public int delete(String code, String compCode) {
        String strSql = "delete from Currency o where o.key.code = '" + code
                + "' and o.key.compId = " + compCode;
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }
}
