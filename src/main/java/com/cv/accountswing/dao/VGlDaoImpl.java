/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.view.VGl;
import com.cv.accountswing.util.Util1;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 *
 * @author WSwe
 */
@Repository
public class VGlDaoImpl extends AbstractDao<Long, VGl> implements VGlDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(VGlDaoImpl.class);

    @Override
    public List<VGl> find(String glVouNo) {
        List<VGl> listVGL = search("-", "-", "-", "-", "-", "-", "-", "-", "-",
                "-", "-", "-", "-", "GV", glVouNo, "-", "-", "-", "-", "-", "-");
        return listVGL;
    }

    @Override
    public List<VGl> search(String from, String to, String desp, String sourceAcId,
            String acId, String frmCurr, String toCurr, String reference, String dept,
            String vouNo, String cvId, String chequeNo, String compCode, String tranSource,
            String glVouNo, String deptName, String traderName, String splitId,
            String projectId, String debAmt, String crdAmt) {
        String strSql = "select o from VGl o ";
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

        if (!desp.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.description like '" + desp + "%'";
            } else {
                strFilter = strFilter + " and o.description like '" + desp + "%'";
            }
        }

        if (!sourceAcId.equals("-") && !acId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "(o.sourceAcId = '" + sourceAcId
                        + "' or o.sourceAccParent = '" + sourceAcId + "') and (o.accountId = '" + acId
                        + "' or o.accParent = '" + acId + "')";
            } else {
                strFilter = strFilter + " and (o.sourceAcId = '" + sourceAcId
                        + "' or o.sourceAccParent = '" + sourceAcId + "') and (o.accountId = '" + acId
                        + "' or o.accParent = '" + acId + "')";
            }
        } else if (!sourceAcId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "(o.sourceAcId = '" + sourceAcId + "' or o.accountId = '" + sourceAcId
                        + "' or o.sourceAccParent = '" + sourceAcId + "' or o.accParent = '" + sourceAcId + "')";
            } else {
                strFilter = strFilter + " and (o.sourceAcId = '" + sourceAcId + "' or o.accountId = '" + sourceAcId
                        + "' or o.sourceAccParent = '" + sourceAcId + "' or o.accParent = '" + sourceAcId + "')";
            }
        }

        if (!projectId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.projectId = " + projectId;
            } else {
                strFilter = strFilter + " and o.projectId = " + projectId;
            }
        }

        if (!frmCurr.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.fromCurId = '" + frmCurr + "'";
            } else {
                strFilter = strFilter + " and o.fromCurId = '" + frmCurr + "'";
            }
        }

        if (!toCurr.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.toCurId = '" + toCurr + "'";
            } else {
                strFilter = strFilter + " and o.toCurId = '" + toCurr + "'";
            }
        }

        if (!reference.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.reference like '" + reference + "%'";
            } else {
                strFilter = strFilter + " and o.reference like '" + reference + "%'";
            }
        }

        if (!dept.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.deptId = '" + dept + "'";
            } else {
                strFilter = strFilter + " and o.deptId = '" + dept + "'";
            }
        }

        if (!vouNo.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.vouNo like '%" + vouNo + "%'";
            } else {
                strFilter = strFilter + " and o.vouNo like '%" + vouNo + "%'";
            }
        }

        /*if(!cvId.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.traderCode like '%" + cvId + "%'";
            }else{
                strFilter = strFilter + " and o.traderCode like '%" + cvId + "%'";
            }
        }*/
        if (!cvId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.traderId = " + cvId;
            } else {
                strFilter = strFilter + " and o.traderId = " + cvId;
            }
        }

        if (!chequeNo.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.chequeNo like '%" + chequeNo + "%'";
            } else {
                strFilter = strFilter + " and o.chequeNo like '%" + chequeNo + "%'";
            }
        }

        if (!compCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.compId = " + compCode;
            } else {
                strFilter = strFilter + " and o.compId = " + compCode;
            }
        }

        if (!tranSource.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.tranSource = '" + tranSource + "'";
            } else {
                strFilter = strFilter + " and o.tranSource = '" + tranSource + "'";
            }
        } else {
            if (strFilter.isEmpty()) {
                strFilter = "(o.tranSource <> 'OPENING' or o.tranSource is null)";
            } else {
                strFilter = strFilter + " and (o.tranSource <> 'OPENING' or o.tranSource is null)";
            }
        }

        if (!glVouNo.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.glVouNo = '" + glVouNo + "'";
            } else {
                strFilter = strFilter + " and o.glVouNo = '" + glVouNo + "'";
            }
        }

        if (!deptName.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.deptName like '%" + deptName + "%'";
            } else {
                strFilter = strFilter + " and o.deptName like '%" + deptName + "%'";
            }
        }

        if (!traderName.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.traderName like '%" + traderName + "%'";
            } else {
                strFilter = strFilter + " and o.traderName like '%" + traderName + "%'";
            }
        }

        if (!splitId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.splitId = " + splitId;
            } else {
                strFilter = strFilter + " and o.splitId = " + splitId;
            }
        }
        if (!debAmt.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.drAmt = '" + debAmt + "'";
            } else {
                strFilter = strFilter + " and o.drAmt = '" + debAmt + "'";
            }
        }
        if (!crdAmt.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.crAmt = '" + crdAmt + "'";
            } else {
                strFilter = strFilter + " and o.crAmt = '" + crdAmt + "'";
            }
        }

        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter + " order by o.glId";
        }

        List<VGl> listVGL = findHSQL(strSql);
        return listVGL;
    }

    @Override
    public List<VGl> searchGlDrCr(String from, String to, String sourceAcId,
            String frmCurr, String dept, String cvId, String compCode, String option) {
        String strSql = "select o from VGl o ";
        String strFilter = "";

        if (!from.equals("-") && !to.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "date(o.glDate) between '" + Util1.toDateStrMYSQL(from, "dd/MM/yyyy")
                        + "' and '" + Util1.toDateStrMYSQL(to, "dd/MM/yyyy") + "'";
            } else {
                strFilter = strFilter + " and o.glDate between '"
                        + Util1.toDateStrMYSQL(from, "dd/MM/yyyy") + "' and '" + Util1.toDateStrMYSQL(to, "dd/MM/yyyy") + "'";
            }
        } else if (!from.endsWith("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "date(o.glDate) >= '" + Util1.toDateStrMYSQL(from, "dd/MM/yyyy") + "'";
            } else {
                strFilter = strFilter + " and o.glDate >= '" + Util1.toDateStrMYSQL(from, "dd/MM/yyyy") + "'";
            }
        } else if (!to.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "date(o.glDate) <= '" + Util1.toDateStrMYSQL(to, "dd/MM/yyyy") + "'";
            } else {
                strFilter = strFilter + " and o.glDate <= '" + Util1.toDateStrMYSQL(to, "dd/MM/yyyy") + "'";
            }
        }

        if (!sourceAcId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "(o.sourceAcId = '" + sourceAcId + "' or o.accountId = '" + sourceAcId + "')";
            } else {
                strFilter = strFilter + " and (o.sourceAcId = '" + sourceAcId + "' or o.accountId = '" + sourceAcId + "')";
            }
        }

        if (!frmCurr.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.fromCurId = '" + frmCurr + "'";
            } else {
                strFilter = strFilter + " and o.fromCurId = '" + frmCurr + "'";
            }
        }

        if (!dept.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.deptId = '" + dept + "'";
            } else {
                strFilter = strFilter + " and o.deptId = '" + dept + "'";
            }
        }

        if (!cvId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.traderId ='" + cvId + "' ";
            } else {
                strFilter = strFilter + " and o.traderId ='" + cvId + "' ";
            }
        }

        if (!compCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.compId = '" + compCode + "'";
            } else {
                strFilter = strFilter + " and o.compId = '" + compCode + "'";
            }
        }

        /*if(option.equals("DR")){
            if(strFilter.isEmpty()){
                strFilter = "o.drAmt > 0 and (o.crAmt = 0 or o.crAmt is null)";
            }else{
                strFilter = strFilter + " and o.drAmt > 0 and (o.crAmt = 0 or o.crAmt is null)";
            }
        }else{
            if(strFilter.isEmpty()){
                strFilter = "o.crAmt > 0 and (o.drAmt = 0 or o.drAmt is null)";
            }else{
                strFilter = strFilter + " and o.crAmt > 0 and (o.drAmt = 0 or o.drAmt is null)";
            }
        }*/
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }
        LOGGER.info("VGL Search Query :" + strSql);
        List<VGl> listVGL = findHSQL(strSql);
        return listVGL;
    }

    @Override
    public List<VGl> getCrDrVoucher(String vouNo, String compCode) {
        String strSql = "select o from VGl o where o.vouNo = '" + vouNo
                + "' and o.compId = '" + compCode + "' order by o.glId";
        List<VGl> listVGL = findHSQL(strSql);
        return listVGL;
    }

    @Override
    public VGl findById(Long glId) {
        return getByKey(glId);
    }
}
