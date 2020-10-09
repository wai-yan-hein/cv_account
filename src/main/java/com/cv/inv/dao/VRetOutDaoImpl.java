/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.accountswing.dao.AbstractDao;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.view.VRetOut;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author lenovo
 */
@Repository
public class VRetOutDaoImpl extends AbstractDao<String,VRetOut> implements VRetOutDao {

    @Override
    public List<VRetOut> search(String fDate, String tDate, String cusId, String locId, String vouNo, String stockCodes, String compCode) {
         String strSql = "select distinct o from VRetOut o";
        String strFilter = "";
        if (!fDate.equals("-") && !fDate.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.glDate between '" + Util1.toDateStrMYSQL(fDate, "dd/MM/yyyy")
                        + "' and '" + Util1.toDateStrMYSQL(tDate, "dd/MM/yyyy") + "'";
            } else {
                strFilter = strFilter + " and o.glDate between '"
                        + Util1.toDateStrMYSQL(fDate, "dd/MM/yyyy") + "' and '" + Util1.toDateStrMYSQL(tDate, "dd/MM/yyyy") + "'";
            }
        } else if (!fDate.endsWith("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.glDate >= '" + Util1.toDateStrMYSQL(fDate, "dd/MM/yyyy") + "'";
            } else {
                strFilter = strFilter + " and o.glDate >= '" + Util1.toDateStrMYSQL(fDate) + "'";
            }
        } else if (!tDate.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.glDate <= '" + Util1.toDateStrMYSQL(tDate) + "'";
            } else {
                strFilter = strFilter + " and o.glDate <= '" + Util1.toDateStrMYSQL(tDate, "dd/MM/yyyy") + "'";
            }
        }

        if (!cusId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.traderId = '" + cusId + "'";
            } else {
                strFilter = strFilter + " and o.traderId = '" + cusId + "'";
            }
        }
        if (!locId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.locationId = '" + locId + "'";
            } else {
                strFilter = strFilter + " and o.locationId = '" + locId + "'";
            }
        }

        if (!vouNo.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.vouNo like '%" + vouNo + "%'";
            } else {
                strFilter = strFilter + " and o.vouNo like '%" + vouNo + "%'";
            }
        }
        if (!compCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.compId = " + compCode;
            } else {
                strFilter = strFilter + " and o.compId = " + compCode;
            }
        }
        if (!stockCodes.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.stockId in (" + stockCodes + ")";
            } else {
                strFilter = strFilter + " and o.stockId  in (" + stockCodes + ")";
            }
        }

        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }
        strSql = strSql + " group by o.key.glId order by o.key.glId,o.key.vouNo desc ,date(o.glDate) desc";

        List listDetail = findHSQL(strSql);
        return listDetail;
    }
    
}
