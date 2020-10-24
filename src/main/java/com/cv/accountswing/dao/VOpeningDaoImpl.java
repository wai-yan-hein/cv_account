/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.view.VCOAOpening;
import com.cv.accountswing.util.Util1;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class VOpeningDaoImpl extends AbstractDao<Integer, VCOAOpening> implements VOpeningDao {

    @Override
    public List<VCOAOpening> search(String opDate, String sourceAccId, String userId, String compId, String depId,String curCode) {
        String hsql = "select o from VCOAOpening o";
        String strFilter = "";
        if (!opDate.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.opDate = '" + Util1.toDateStr(opDate, "dd/MM/yyyy", "yyyy-MM-dd") + "'";
            } else {
                strFilter = strFilter + " and o.opDate = '" + Util1.toDateStr(opDate, "dd/MM/yyyy", "yyyy-MM-dd") + "'";
            }
        }
        if (!sourceAccId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.sourceAccId = '" + sourceAccId + "'";
            } else {
                strFilter = strFilter + " and o.sourceAccId = '" + sourceAccId + "'";
            }
        }
        if (!userId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.userId = '" + userId + "'";
            } else {
                strFilter = strFilter + " and o.userId = '" + userId + "'";
            }
        }
        if (!compId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.compId = '" + compId + "'";
            } else {
                strFilter = strFilter + " and o.compId = '" + compId + "'";
            }
        }
        if (!depId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.depCode = '" + depId + "'";
            } else {
                strFilter = strFilter + " and o.depCode = '" + depId + "'";
            }
        }
        if (!curCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.curCode = '" + curCode + "'";
            } else {
                strFilter = strFilter + " and o.curCode = '" + curCode + "'";
            }
        }
        if (!strFilter.isEmpty()) {
            hsql = hsql + " where " + strFilter;
        }
        List<VCOAOpening> listOpening = findHSQL(hsql);
        return listOpening;
    }

}
