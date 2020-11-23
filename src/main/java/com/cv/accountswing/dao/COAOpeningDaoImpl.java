/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.AccOpeningH;
import com.cv.accountswing.util.Util1;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author winswe
 */
@Repository
public class COAOpeningDaoImpl extends AbstractDao<Long, AccOpeningH> implements COAOpeningDao {

    @Override
    public AccOpeningH save(AccOpeningH aoh) {
        persist(aoh);
        return aoh;
    }

    @Override
    public AccOpeningH findById(Long Id) {
        AccOpeningH aoh = getByKey(Id);
        return aoh;
    }

    @Override
    public List<AccOpeningH> search(String from, String to, String compCode,
            String currency, String remark) {
        String strSql = "select o from AccOpeningH o ";
        String strFilter = "";

        if (!from.equals("-") && !to.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.opDate between '" + Util1.toDateStr(from, "dd/MM/yyyy", "yyyy-MM-dd")
                        + "' and '" + Util1.toDateStr(to, "dd/MM/yyyy", "yyyy-MM-dd") + "'";
            } else {
                strFilter = strFilter + " and o.opDate between '"
                        + Util1.toDateStr(from, "dd/MM/yyyy", "yyyy-MM-dd") + "' and '"
                        + Util1.toDateStr(to, "dd/MM/yyyy", "yyyy-MM-dd") + "'";
            }
        } else if (!from.endsWith("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.opDate >= '" + Util1.toDateStr(from, "dd/MM/yyyy", "yyyy-MM-dd") + "'";
            } else {
                strFilter = strFilter + " and o.opDate >= '"
                        + Util1.toDateStr(from, "dd/MM/yyyy", "yyyy-MM-dd") + "'";
            }
        } else if (!to.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.opDate <= '" + Util1.toDateStr(to, "dd/MM/yyyy", "yyyy-MM-dd") + "'";
            } else {
                strFilter = strFilter + " and o.opDate <= '"
                        + Util1.toDateStr(to, "dd/MM/yyyy", "yyyy-MM-dd") + "'";
            }
        }

        if (!compCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.compCode = " + compCode;
            } else {
                strFilter = strFilter + " and o.compCode = " + compCode;
            }
        }

        if (!currency.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.curr = '" + currency + "'";
            } else {
                strFilter = strFilter + " and o.curr = '" + currency + "'";
            }
        }

        if (!remark.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.remark = '" + remark + "'";
            } else {
                strFilter = strFilter + " and o.remark = '" + remark + "'";
            }
        }

        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }

        List<AccOpeningH> listAOH = findHSQL(strSql);
        return listAOH;
    }

    @Override
    public void GenerateZero(String tranIdH, String compCode, String currCode) throws Exception {
        String strSql = "insert into acc_opening_d(tran_id_h, coa_code, curr_code, dr_amt, cr_amt) "
                + "select " + tranIdH + ", coa_code, cur_code, 0, 0 "
                + "from v_coa_opening where comp_code = '" + compCode
                + "' and cur_code = '" + currCode + "'";
        execSQL(strSql);
    }

    @Override
    public void deleteOpening(Long id) throws Exception {
        String strSql1 = "delete from acc_opening_d where tran_id_h = " + id;
        String strSql2 = "delete from acc_opening_h where tran_id = " + id;

        execSQL(strSql1, strSql2);
    }

    @Override
    public void GenerateZeroGL(String opDate, String userId, String compCode,
            String currCode, String dept, String coaGroup) throws Exception {
        /*String strSql = "insert into gl(gl_date, source_ac_id, from_cur_id, dr_amt, " +
                "cr_amt, user_id, comp_id, tran_source, created_date) " +
                "select '" + opDate + "', coa_code, cur_code, 0, 0, '" + userId + "', '" +
                compCode + "', 'OPENING', sysdate() from v_coa_curr where level >= 3 and cur_code = '" + currCode + 
                "' and coa_code not in (select coa_code from coa_excludion where option_desp = 'COAOPENING')";*/

        if (!dept.equals("-") && !currCode.equals("-")) {
            String strSql = "insert into gl(gl_date, source_ac_id, from_cur_id, dr_amt, "
                    + "cr_amt, user_id, comp_id, tran_source, created_date, dept_id) "
                    + "select '" + opDate + "', child_coa_code, cur_code, 0, 0, '" + userId + "', '"
                    + compCode + "', 'OPENING', sysdate(), '" + dept
                    + "' from v_coa_tree where level2 >= 3 and cur_code = '" + currCode
                    + "' and coa_code not in (select coa_code from coa_excludion where option_desp = 'COAOPENING')"
                    + " and comp_code1 = '" + compCode + "' and coa_parent1 in (" + coaGroup + ")";
            execSQL(strSql);

            String strSql1 = "insert into gl(gl_date, source_ac_id, from_cur_id, dr_amt, "
                    + "cr_amt, user_id, comp_id, tran_source, created_date, dept_id, cv_id) "
                    + "select '" + opDate + "', coa_code, '" + currCode + "', 0, 0, '" + userId + "', '"
                    + compCode + "', 'OPENING', sysdate(), '" + dept + "', cv_id"
                    + " from v_cv_coa where comp_code = " + compCode;
            execSQL(strSql1);
        }

    }

    @Override
    public void deleteOpeningGL(String opDate, String compCode, String currCode, String dept) throws Exception {
        String strSql = "delete from gl where gl_date = '" + opDate + "' and tran_source = 'OPENING' and "
                + "comp_id = " + compCode + " and from_cur_id = '" + currCode + "'";

        if (!dept.equals("-")) {
            strSql = strSql + " and dept_id = '" + dept + "'";
        }
        execSQL(strSql);
    }

    @Override
    public void insertCoaOpening(String opDate, String compCode, String currCode,
            String dept, String userId) throws Exception {
        String strSql = "insert into gl(gl_date, source_ac_id, from_cur_id, dr_amt, "
                + "cr_amt, user_id, comp_id, tran_source, created_date, dept_id) "
                + "select '" + opDate + "', coa_code, cur_code, 0, 0, '" + userId + "', '"
                + compCode + "', 'OPENING', sysdate(), dept_code "
                + " from v_coa_curr_dept where level >= 3 "
                + "and (cur_code = '" + currCode + "' or '" + currCode + "' = '-')"
                + " and (dept_code = '" + dept + "' or '" + dept + "' = '-')"
                + " and coa_code not in (select coa_code from coa_excludion where option_desp = 'COAOPENING')"
                + " and comp_code = " + compCode;
        execSQL(strSql);
    }

    @Override
    public void generateZeroOpening(String opDate, String userId, String compCode, String currCode, String dept, String coaGroup) throws Exception {
        if (!dept.equals("-") && !currCode.equals("-")) {
            String strSql = "insert into coa_opening(op_date, source_acc_id, cur_code, dr_amt, "
                    + "cr_amt, user_id, comp_id, tran_source, created_date, dept_code) "
                    + "select '" + opDate + "', child_coa_code, '" + currCode + "', 0, 0, '" + userId + "', '"
                    + compCode + "', 'OPENING', sysdate(), '" + dept
                    + "' from v_coa_tree where level2 >= 3 \n"
                    + "and coa_code not in (select coa_code from coa_excludion where option_desp = 'COAOPENING')"
                    + " and comp_code1 = '" + compCode + "' and coa_parent1 in (" + coaGroup + ")";
            execSQL(strSql);

            String strSql1 = "insert into coa_opening(op_date, source_acc_id, cur_code, dr_amt, "
                    + "cr_amt, user_id, comp_id, tran_source, created_date, dept_code, cv_id) "
                    + "select '" + opDate + "', coa_code, '" + currCode + "', 0, 0, '" + userId + "', '"
                    + compCode + "', 'OPENING', sysdate(), '" + dept + "', cv_id"
                    + " from v_cv_coa where comp_code = " + compCode;
            execSQL(strSql1);
        }
    }

    @Override
    public void deleteOpening(String opDate, String compCode, String currCode, String dept) throws Exception {
        String strSql = "delete from coa_opening where op_date = '" + opDate + "' and tran_source = 'OPENING' and "
                + "comp_id = " + compCode + " and cur_code = '" + currCode + "'";

        if (!dept.equals("-")) {
            strSql = strSql + " and dept_code = '" + dept + "'";
        }
        execSQL(strSql);
    }
}
