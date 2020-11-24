/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.Gl;
import com.cv.accountswing.util.Util1;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author winswe
 */
@Repository
public class GlDaoImpl extends AbstractDao<Long, Gl> implements GlDao {

    @Override
    public Gl save(Gl gl) throws Exception {
        if (gl.getGlId() != null) {
            //String strBakSql = getGlLogSql(gl.getGlId(),"GL-UPDATE");
            //execSQL(strBakSql);
        }

        persist(gl);
        return gl;
    }

    @Override
    public List<Gl> saveBatchGL(List<Gl> listGL) throws Exception {
        if (listGL != null) {
            if (!listGL.isEmpty()) {
                saveBatch(listGL);
            }
        }

        return listGL;
    }

    @Override
    public Gl findById(Long glId) {
        Gl gl = getByKey(glId);
        return gl;
    }

    @Override
    public List<Gl> search(String from, String to, String desp, String sourceAcId,
            String acId, String frmCurr, String toCurr, String reference, String dept,
            String vouNo, String cvId, String chequeNo, String compCode, String tranSource,
            String glVouNo, String splitId, String projectId) {
        String strSql = "select o from Gl o ";
        String strFilter = "";

        if (!from.equals("-") && !to.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.glDate between '" + Util1.toDateStrMYSQL(from)
                        + "' and '" + Util1.toDateStrMYSQL(to) + "'";
            } else {
                strFilter = strFilter + " and o.glDate between '"
                        + Util1.toDateStrMYSQL(from) + "' and '" + Util1.toDateStrMYSQL(to) + "'";
            }
        } else if (!from.endsWith("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.glDate >= '" + Util1.toDateStrMYSQL(from) + "'";
            } else {
                strFilter = strFilter + " and o.glDate >= '" + Util1.toDateStrMYSQL(from) + "'";
            }
        } else if (!to.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.glDate <= '" + Util1.toDateStrMYSQL(to) + "'";
            } else {
                strFilter = strFilter + " and o.glDate <= '" + Util1.toDateStrMYSQL(to) + "'";
            }
        }

        if (!desp.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.description like '" + desp + "'";
            } else {
                strFilter = strFilter + " and o.description like '" + desp + "'";
            }
        }

        if (!sourceAcId.equals("-") && !acId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.sourceAcId = '" + sourceAcId + "' and a.accountId = '" + acId + "'";
            } else {
                strFilter = strFilter + " and o.sourceAcId = '" + sourceAcId + "' and a.accountId = '" + acId + "'";
            }
        } else if (!sourceAcId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.sourceAcId = '" + sourceAcId + "' or a.accountId = '" + sourceAcId + "'";
            } else {
                strFilter = strFilter + " and o.sourceAcId = '" + sourceAcId + "' or a.accountId = '" + sourceAcId + "'";
            }
        }

        if (!projectId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.projectId = " + projectId;
            } else {
                strFilter = strFilter + " and o.projectId = " + acId;
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
                strFilter = "o.reference like '%" + reference + "%'";
            } else {
                strFilter = strFilter + " and o.reference like '%" + reference + "%'";
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
                strFilter = "o.compCode = " + compCode;
            } else {
                strFilter = strFilter + " and o.compCode = " + compCode;
            }
        }

        if (!tranSource.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.tranSource = '" + tranSource + "'";
            } else {
                strFilter = strFilter + " and o.tranSource = '" + tranSource + "'";
            }
        }

        if (!glVouNo.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.glVouNo = '" + glVouNo + "'";
            } else {
                strFilter = strFilter + " and o.glVouNo = '" + glVouNo + "'";
            }
        }

        if (!splitId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.splitId = '" + splitId + "'";
            } else {
                strFilter = strFilter + " and o.splitId = '" + splitId + "'";
            }
        }

        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }

        List<Gl> listGL = findHSQL(strSql);
        return listGL;
    }

    @Override
    public int delete(Long glId, String option) throws Exception {
        String strBakSql = getGlLogSql(glId, option);
        execSQL(strBakSql);

        String strSql = "delete from Gl o where o.glId = " + glId;
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }
}
