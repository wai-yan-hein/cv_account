/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.view.VGeneralVoucher;
import com.cv.accountswing.util.Util1;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author winswe
 */
@Repository
public class VGvDaoImpl extends AbstractDao<String, VGeneralVoucher> implements VGvDao {

    @Override
    public List<VGeneralVoucher> search(String from, String to, String vouNo,
            String ref, String compCode, String projectId) {
        String strFilter = "";

        if (!from.equals("-") && !to.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.glDate between '" + Util1.toDateStrMYSQL(from, "dd/MM/yyyy")
                        + "' and '" + Util1.toDateStrMYSQL(to, "dd/MM/yyyy") + "'";
            } else {
                strFilter = strFilter + " and o.glDate between '"
                        + Util1.toDateStrMYSQL(from, "dd/MM/yyyy") + "' and '" + Util1.toDateStrMYSQL(to, "dd/MM/yyyy") + "'";
            }
        } else if (!from.endsWith("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.glDate >= '" + Util1.toDateStrMYSQL(from, "dd/MM/yyyy") + "'";
            } else {
                strFilter = strFilter + " and o.glDate >= '" + Util1.toDateStrMYSQL(from, "dd/MM/yyyy") + "'";
            }
        } else if (!to.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.glDate <= '" + Util1.toDateStrMYSQL(to, "dd/MM/yyyy") + "'";
            } else {
                strFilter = strFilter + " and o.glDate <= '" + Util1.toDateStrMYSQL(to, "dd/MM/yyyy") + "'";
            }
        }

        if (!vouNo.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.vouNo = '" + vouNo + "'";
            } else {
                strFilter = strFilter + " and o.vouNo = '" + vouNo + "'";
            }
        }

        if (!ref.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.reference = '" + ref + "'";
            } else {
                strFilter = strFilter + " and o.reference = '" + ref + "'";
            }
        }

        if (!compCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.compId = '" + compCode + "'";
            } else {
                strFilter = strFilter + " and o.compId = '" + compCode + "'";
            }
        }

        if (!projectId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.projectId = '" + projectId + "'";
            } else {
                strFilter = strFilter + " and o.projectId = '" + projectId + "'";
            }
        }

        String strSql = "select o from VGeneralVoucher o";
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }

        List<VGeneralVoucher> listVGV = findHSQL(strSql);
        return listVGV;
    }
}
