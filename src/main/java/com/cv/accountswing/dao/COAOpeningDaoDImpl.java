
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.AccOpeningD;
import com.cv.accountswing.entity.temp.TmpOpeningClosing;
import com.cv.accountswing.entity.view.VAccOpeningD;
import com.cv.accountswing.util.Util1;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 *
 * @author winswe
 */
@Repository
public class COAOpeningDaoDImpl extends AbstractDao<Long, AccOpeningD> implements COAOpeningDaoD {

    private static final Logger logger = LoggerFactory.getLogger(COAOpeningDaoDImpl.class);

    @Override
    public AccOpeningD save(AccOpeningD aod) {
        persist(aod);
        return aod;
    }

    @Override
    public AccOpeningD findById(Long id) {
        AccOpeningD aod = getByKey(id);
        return aod;
    }

    @Override
    public List<VAccOpeningD> search(String tranIdH) {
        String strSql = "select o from VAccOpeningD o where o.tranIdH = " + tranIdH;
        List<VAccOpeningD> listVOAD = findHSQLList(strSql);
        return listVOAD;
    }

    @Override
    public int delete(String tranId) {
        String strSql = "delete from AccOpeningD o where o.tranId = " + tranId;
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }

    @Override
    public void insertFilter(String coaCode, int level, String opDate,
            String curr, String userId) throws Exception {
        String strFilter = "";

        if (!coaCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "coa.coa_code = '" + coaCode + "'";
            } else {
                strFilter = strFilter + " and coa.coa_code = '" + coaCode + "'";
            }
        }

        if (!opDate.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "ifnull(op.op_date, '1900-01-01') <= '"
                        + Util1.toDateStr(opDate, "dd/MM/yyyy", "yyyy-MM-dd") + "'";
            } else {
                strFilter = strFilter + " and ifnull(op.op_date, '1900-01-01') <= '"
                        + Util1.toDateStr(opDate, "dd/MM/yyyy", "yyyy-MM-dd") + "'";
            }
        }

        if (!curr.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "coa.cur_code = '" + curr + "'";
            } else {
                strFilter = strFilter + " and coa.cur_code = '" + curr + "'";
            }
        }

        String strSql = "insert into tmp_op_filter(comp_code, coa_code, op_tran_id_d, "
                + "curr_id, op_date, user_id) "
                + "select coa.comp_code, coa.coa_code, op.tran_id_d, coa.cur_code, "
                + "ifnull(op.op_date, '1900-01-01') op_date,'" + userId + "'"
                + " from (select comp_code, coa_code, cur_code, level\n"
                + "from chart_of_account, currency) coa left join (select m.tran_id_d, m.curr_id, m.op_date, m.coa_code, m.comp_code\n"
                + " from v_coa_opening_by_date m, ("
                + "select a.curr_id, a.coa_code, max(ifnull(a.op_date, '1900-01-01')) op_date "
                + "from v_coa_opening_by_date a group by a.curr_id, a.coa_code) f "
                + "where m.curr_id = f.curr_id and m.coa_code = f.coa_code and m.op_date = f.op_date) op on "
                + "coa.comp_code = op.comp_code and coa.coa_code = op.coa_code and coa.cur_code = op.curr_id "
                + "where coa.comp_code is not null and coa.level >= " + level;

        if (!strFilter.isEmpty()) {
            strSql = strSql + " and " + strFilter;
        }

        //strSql = strSql + " group by coa.comp_code, coa.coa_code, op.tran_id_d, op.curr_id";
        execSQL(strSql);
    }

    @Override
    public void deleteTmp(String userId) throws Exception {
        String strDeleteSql1 = "delete from tmp_op_filter where user_id = '"
                + userId + "'";
        String strDeleteSql2 = "delete from tmp_op_cl where user_id = '" + userId + "'";

        execSQL(strDeleteSql1, strDeleteSql2);
    }

    @Override
    public List<TmpOpeningClosing> getOpBalance(String coaCode, int level, String opDate,
            String curr, String userId) throws Exception {
        //deleteTmp(userId);
        /*String strDeleteSql1 = "delete from tmp_op_filter where user_id = '" +
         userId + "'";
         String strDeleteSql2 = "delete from tmp_op_cl where user_id = '" + userId + "'";
        
         execSQL(strDeleteSql1, strDeleteSql2);*/
        insertFilter(coaCode, level, opDate, curr, userId);

        String strSql = "insert into tmp_op_cl(coa_id, curr_id, user_id, opening, dr_amt, cr_amt) "
                + "select a.coa_code, a.curr_id, '" + userId + "', sum(ifnull(a.balance,0)), 0, 0 "
                + "from (select tof.comp_code, tof.coa_code, tof.curr_id, aod.ex_rate, aod.dr_amt, "
                + "aod.cr_amt,(ifnull(aod.dr_amt,0)-ifnull(aod.cr_amt,0)) balance,"
                + "ifnull(tof.op_date, date('1900-01-01')) tran_date, 'Opening' tran_option, tof.user_id "
                + "from tmp_op_filter tof, acc_opening_d aod "
                + "where tof.coa_code = aod.coa_code and tof.op_tran_id_d = aod.tran_id "
                + "and tof.curr_id = aod.curr_code and tof.user_id = '" + userId + "' "
                + "union all "
                + "select tof.comp_code, tof.coa_code, tof.curr_id, gl.ex_rate, "
                + "gl.dr_amt, gl.cr_amt, (ifnull(gl.dr_amt,0)-ifnull(gl.cr_amt,0)) balance, "
                + "gl.gl_date tran_date, 'GL' tran_option, tof.user_id "
                + "from tmp_op_filter tof, gl "
                + "where tof.coa_code = source_ac_id and tof.curr_id = from_cur_id "
                + "and gl_date >= tof.op_date and gl_date < '"
                + Util1.toDateStr(opDate, "dd/MM/yyyy", "yyyy-MM-dd")
                + "' and tof.user_id = '" + userId + "') a group by a.coa_code, a.curr_id";

        //logger.info((strSql));
        execSQL(strSql);

        String strHSql = "select o from TmpOpeningClosing o where o.key.userId = '" + userId + "'";
        List<TmpOpeningClosing> listTOC = findHSQLList(strHSql);

        return listTOC;
    }

    @Override
    public void insertFilterGL(String coaCode, String opDate, int level,
            String curr, String userId) throws Exception {
        String strFilter = "";

        if (!coaCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "gl.source_ac_id = '" + coaCode + "'";
            } else {
                strFilter = strFilter + " and gl.source_ac_id = '" + coaCode + "'";
            }
        }

        if (!opDate.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "ifnull(gl_date, '1900-01-01') = '"
                        + Util1.toDateStr(opDate, "dd/MM/yyyy", "yyyy-MM-dd") + "'";
            } else {
                strFilter = strFilter + " and ifnull(gl_date, '1900-01-01') = '"
                        + Util1.toDateStr(opDate, "dd/MM/yyyy", "yyyy-MM-dd") + "'";
            }
        }

        if (!curr.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "from_cur_id = '" + curr + "'";
            } else {
                strFilter = strFilter + " and from_cur_id = '" + curr + "'";
            }
        }

        if (strFilter.isEmpty()) {
            strFilter = "tran_source = 'OPENING'";
        } else {
            strFilter = strFilter + " and tran_source = 'OPENING'";
        }

        String strSql = "insert into tmp_op_filter(comp_code, coa_code, op_tran_id_d, curr_id, op_date, user_id) "
                + "select gl.comp_id, source_ac_id, gl_id, from_cur_id, gl_date, '" + userId + "' "
                + "from gl, chart_of_account coa where gl.comp_id = coa.comp_code "
                + "and gl.source_ac_id = coa.coa_code and coa.level >= " + level;

        if (!strFilter.isEmpty()) {
            strSql = strSql + " and " + strFilter;
        }

        execSQL(strSql);
    }

    @Override
    public void genOpBalanceGL(String coaCode, String opDate,
            String clDate, int level, String curr, String userId, String dept) throws Exception {
        deleteTmp(userId);
        insertFilterGL(coaCode, opDate, level, curr, userId);
        String strSql = "insert into tmp_op_cl(coa_id, curr_id, user_id, opening, dr_amt, cr_amt) \n"
                + "select coa_code, curr_id, '" + userId + "', sum(balance), 0, 0 \n"
                + "from (\n"
                + "select tof.coa_code, tof.curr_id, ifnull(gl.dr_amt,0)-ifnull(gl.cr_amt,0) balance,\n"
                + "ifnull(gl.dr_amt,0) dr_amt, ifnull(gl.cr_amt,0) cr_amt,tof.cv_id\n"
                + "from tmp_op_filter tof, gl\n"
                + "where tof.op_tran_id_d = gl.gl_id and tof.comp_code = gl.comp_id and tof.curr_id = gl.from_cur_id and \n"
                + "tof.coa_code = gl.source_ac_id and tof.op_date = gl.gl_date and tof.user_id = '" + userId + "' and (gl.dept_id = '" + dept + "' or '-' = '" + dept + "')\n"
                + "union all\n"
                + "select tof.coa_code, tof.curr_id, get_dr_cr_amt(gl.source_ac_id, gl.account_id, tof.coa_code, ifnull(gl.dr_amt,0), ifnull(gl.cr_amt,0), 'DR')-\n"
                + "get_dr_cr_amt(gl.source_ac_id, gl.account_id, tof.coa_code, ifnull(gl.dr_amt,0), ifnull(gl.cr_amt,0), 'CR') balance, \n"
                + "ifnull(gl.dr_amt,0) dr_amt, ifnull(gl.cr_amt,0),tof.cv_id \n"
                + "from tmp_op_filter tof, gl\n"
                + "where tof.comp_code = gl.comp_id "
                + "and (tof.coa_code = gl.source_ac_id or tof.coa_code = gl.account_id) "
                + "and ifnull(gl.tran_source,'-') <> 'OPENING' and \n"
                + "tof.curr_id = gl.from_cur_id and gl.gl_date >= '" + opDate + "' and gl.gl_date < '"
                + Util1.toDateStrMYSQL(clDate, "dd/MM/yyyy")
                + "' and tof.user_id = '" + userId + "' and (gl.dept_id = '" + dept + "' or '-' = '" + dept + "')) a \n"
                + "group by coa_code, curr_id";
        execSQL(strSql);
    }

    @Override
    public List<TmpOpeningClosing> getOpBalanceGL(String userId) {
        String strHSql = "select o from TmpOpeningClosing o where o.key.userId = '" + userId + "'";
        List<TmpOpeningClosing> listTOC = findHSQLList(strHSql);
        return listTOC;
    }

    @Override
    public void genTriBalance(String compCode, String fromDate, String opDate, String tranDate,
            String coaCode, String currency, String dept, String cvId, String userId) throws Exception {
        deleteTmp(userId);

        String strSqlFilter = "insert into tmp_op_filter(comp_code, coa_code, dept_id, curr_id, "
                + "tran_source, op_date, user_id)\n"
                + "select vcc.comp_code, vcc.coa_code, ifnull(a.dept_id, '-') dept_id, vcc.cur_code, "
                + "'OPENING' as tran_source, ifnull(a.op_date, '1900-01-01') op_date,\n"
                + "'" + userId + "' "
                + "from v_coa_curr vcc left join (\n"
                + "select comp_id, source_ac_id, ifnull(dept_id, '-') as dept_id, from_cur_id, \n"
                + "ifnull(gl.tran_source, '-') tran_source, max(gl_date) as op_date\n"
                + "from gl\n"
                + "where gl.gl_date = '" + opDate + "' and ifnull(gl.tran_source, '-') = 'OPENING'\n"
                + " and (gl.comp_id = " + compCode + " or '-' = '" + compCode + "')"
                //+ " and (gl.source_ac_id = '" + coaCode + "' or '-' = '" + coaCode + "')"
                + " and (gl.from_cur_id = '" + currency + "' or '-' = '" + currency + "')"
                + " and (gl.dept_id = '" + dept + "' or '-' = '" + dept + "')"
                + " and (ifnull(gl.cv_id, -1)  = " + cvId + " or -1 = " + cvId + ")"
                + "group by comp_id, source_ac_id, dept_id, from_cur_id, gl.tran_source) a\n"
                + "on vcc.comp_code = a.comp_id and vcc.coa_code = a.source_ac_id and vcc.cur_code = a.from_cur_id\n"
                + "where vcc.level >= 3"
                + " and (vcc.coa_code = '" + coaCode + "' or '-' = '" + coaCode + "' or vcc.coa_parent = '" + coaCode + "')"
                + " and (vcc.cur_code = '" + currency + "' or '-' = '" + currency + "')";
        execSQL(strSqlFilter);

        if (!cvId.equals("-1")) {
            execSQL("delete from tmp_op_filter where user_id = '" + userId + "' and cv_id <> " + cvId);
        }

        String strSql = "insert into tmp_op_cl(coa_id, curr_id, user_id, dr_amt, cr_amt) \n"
                + "select coa_code, curr_id, '" + userId + "', if(sum(dr_amt-cr_amt)>0, sum(dr_amt-cr_amt),0), if(sum(dr_amt-cr_amt)<0, sum(dr_amt-cr_amt)*-1,0) \n"
                + "from (\n"
                + "	select tof.coa_code, tof.curr_id,\n"
                + "		   sum(ifnull(gl.dr_amt,0)) dr_amt, sum(ifnull(gl.cr_amt,0)) cr_amt\n"
                + "	  from tmp_op_filter tof, gl\n"
                + "	 where tof.comp_code = gl.comp_id and tof.curr_id = gl.from_cur_id and \n"
                + "          tof.dept_id = gl.dept_id and tof.tran_source = gl.tran_source and "
                + "	       tof.coa_code = gl.source_ac_id and tof.op_date = gl.gl_date and tof.user_id = '" + userId + "' "
                + "and (gl.dept_id = '" + dept + "' or '-' = '" + dept + "')\n"
                + " and (gl.cv_id = " + cvId + " or -1 = " + cvId + ") \n"
                + " and ifnull(gl.tran_source, '-') = 'OPENING' and gl.gl_date = '" + opDate + "' "
                + "     group by tof.coa_code, tof.curr_id \n"
                + "	 union all\n"
                + "	select tof.coa_code, tof.curr_id, "
                + "	       sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, tof.coa_code, ifnull(gl.dr_amt,0), ifnull(gl.cr_amt,0), 'DR')) dr_amt, "
                + "      sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, tof.coa_code, ifnull(gl.dr_amt,0), ifnull(gl.cr_amt,0), 'CR')) cr_amt\n"
                + "	  from tmp_op_filter tof, gl "
                + "	 where ifnull(gl.tran_source,'-') <> 'OPENING' and gl.gl_date between '" + opDate + "' \n"
                + "      and '" + tranDate + "' and (gl.dept_id = '" + dept + "' or '-' = '" + dept + "')"
                + "        and (gl.cv_id = " + cvId + " or -1 = " + cvId + ") and tof.comp_code = gl.comp_id and (tof.coa_code = gl.source_ac_id or tof.coa_code = gl.account_id) \n"
                + "        and tof.curr_id = gl.from_cur_id and tof.user_id = '" + userId + "'"
                + " group by tof.coa_code, tof.curr_id, gl.source_ac_id, gl.account_id) a \n"
                + "group by coa_code, curr_id";

        execSQL(strSql);

        /*String strSql1 = "update tmp_op_cl toc join ( \n" +
            "select coa_code, curr_id, '" + userId + "' user_id , sum(dr_amt) dr_amt, sum(cr_amt) cr_amt \n" +
            "from (\n" +
            "	select tof.coa_code, tof.curr_id,\n" +
            "		   sum(ifnull(gl.dr_amt,0)) dr_amt, sum(ifnull(gl.cr_amt,0)) cr_amt\n" +
            "	  from tmp_op_filter tof, gl\n" +
            "	 where tof.comp_code = gl.comp_id and tof.curr_id = gl.from_cur_id and \n" +
            "          tof.dept_id = gl.dept_id and tof.tran_source = gl.tran_source and " +
            "	       tof.coa_code = gl.source_ac_id and tof.op_date = gl.gl_date and tof.user_id = '" + userId + "' " +
                       "and (gl.dept_id = '" + dept + "' or '-' = '" + dept + "')\n" +
                       " and (gl.cv_id = " + cvId + " or -1 = " + cvId + ") \n" +
                " and ifnull(gl.tran_source, '-') = 'OPENING' and gl.gl_date = '" + opDate + "' " +
            "     group by tof.coa_code, tof.curr_id \n" + 
            "	 union all\n" +
            "	select tof.coa_code, tof.curr_id, " +
            "	       sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, tof.coa_code, ifnull(gl.dr_amt,0), ifnull(gl.cr_amt,0), 'DR')) dr_amt, " +
                "      sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, tof.coa_code, ifnull(gl.dr_amt,0), ifnull(gl.cr_amt,0), 'CR')) cr_amt\n" +
            "	  from tmp_op_filter tof, gl " +
            "	 where ifnull(gl.tran_source,'-') <> 'OPENING' and gl.gl_date >= '" + opDate + "' \n" +
                    "      and gl.gl_date < '" + fromDate + "' and (gl.dept_id = '" + dept + "' or '-' = '" + dept + "')"
                + "        and (gl.cv_id = " + cvId + " or -1 = " + cvId + ") and tof.comp_code = gl.comp_id and (tof.coa_code = gl.source_ac_id or tof.coa_code = gl.account_id) \n" +
            "        and tof.curr_id = gl.from_cur_id and tof.user_id = '" + userId + "'"
                + " group by tof.coa_code, tof.curr_id, gl.source_ac_id, gl.account_id) a \n" +
            "group by coa_code, curr_id) a on toc.coa_id = a.coa_code and toc.curr_id = a.curr_id and toc.user_id = a.user_id " +
            "set toc.opening = (a.dr_amt - a.cr_amt), toc.closing = (toc.dr_amt - toc.cr_amt) - (a.dr_amt - a.cr_amt) \n" +
            " where toc.user_id = '" + userId + "'";
        
        execSQL(strSql1);*/
        execSQL("delete from tmp_op_cl where user_id = '" + userId + "' and dr_amt = 0 and cr_amt = 0 and opening = 0 and closing = 0");
    }

    @Override
    public void genArAp(String compCode, String fromDate, String opDate, String tranDate,
            String coaCode, String currency, String dept, String cvId, String userId) throws Exception {
        logger.info("Delete : Start");
        deleteTmp(userId);
        logger.info("Delete : End");

        logger.info("tmp_op_filter : Start");
        String strSqlFilter = "insert into tmp_op_filter(comp_code, coa_code, dept_id, curr_id, "
                + "tran_source, op_date, user_id, cv_id)\n"
                + "select vcc.comp_code, vcc.account_code, ifnull(a.dept_id, '-') dept_id, vcc.cur_code, "
                + "'OPENING' as tran_source, ifnull(a.op_date, '1900-01-01') op_date,\n"
                + "'" + userId + "', vcc.id "
                + "from v_trader_curr_dept vcc left join (\n"
                + "select comp_id, source_ac_id, ifnull(dept_id, '-') as dept_id, from_cur_id, \n"
                + "ifnull(gl.tran_source, '-') tran_source, max(gl_date) as op_date, cv_id \n"
                + "from gl\n"
                + "where gl.gl_date = '" + opDate + "' and ifnull(gl.tran_source, '-') = 'OPENING'\n"
                + " and (gl.comp_id = " + compCode + " or '-' = '" + compCode + "')"
                //+ " and (gl.source_ac_id = '" + coaCode + "' or '-' = '" + coaCode + "')"
                + " and (gl.from_cur_id = '" + currency + "' or '-' = '" + currency + "')"
                + " and (gl.dept_id = '" + dept + "' or '-' = '" + dept + "')"
                + " and (ifnull(gl.cv_id, -1)  = " + cvId + " or -1 = " + cvId + ") "
                + " and (gl.cv_id is not null) "
                + "group by comp_id, source_ac_id, dept_id, from_cur_id, gl.tran_source, cv_id) a\n"
                + "on vcc.comp_code = a.comp_id and vcc.account_code = a.source_ac_id and vcc.cur_code = a.from_cur_id\n"
                + " and vcc.id = a.cv_id and vcc.dept_code = a.dept_id "
                + "where "
                + " (vcc.account_code = '" + coaCode + "' or '-' = '" + coaCode + "' or vcc.coa_parent = '" + coaCode + "')"
                + " and (vcc.cur_code = '" + currency + "' or '-' = '" + currency + "')";
        execSQL(strSqlFilter);
        if (!cvId.equals("-1")) {
            execSQL("delete from tmp_op_filter where user_id = '" + userId + "' and cv_id <> " + cvId);
        }
        logger.info("tmp_op_filter : End");

        logger.info("tmp_op_cl_apar : Start");
        String strSql = "insert into tmp_op_cl_apar(coa_id, curr_id, user_id, cv_id, dept_id, dr_amt, cr_amt) \n"
                + "select coa_code, curr_id, '" + userId + "', cv_id, dept_id "
                + ", if(sum(dr_amt-cr_amt)>0, sum(dr_amt-cr_amt),0), if(sum(dr_amt-cr_amt)<0, sum(dr_amt-cr_amt)*-1,0) \n"
                + "from (\n"
                + "	select tof.coa_code, tof.curr_id, tof.cv_id, tof.dept_id, \n"
                + "		   sum(ifnull(gl.dr_amt,0)) dr_amt, sum(ifnull(gl.cr_amt,0)) cr_amt\n"
                + "	  from tmp_op_filter tof, gl\n"
                + "	 where tof.comp_code = gl.comp_id and tof.curr_id = gl.from_cur_id and \n"
                + "          tof.dept_id = gl.dept_id and tof.tran_source = gl.tran_source and "
                + " tof.cv_id = gl.cv_id and "
                + "	       tof.coa_code = gl.source_ac_id and tof.op_date = gl.gl_date and tof.user_id = '" + userId + "' "
                + "and (gl.dept_id = '" + dept + "' or '-' = '" + dept + "')\n"
                + " and (gl.cv_id = " + cvId + " or -1 = " + cvId + ") and gl.gl_date = '" + opDate + "' \n"
                + " and ifnull(gl.tran_source,'-') = 'OPENING' "
                + "     group by tof.coa_code, tof.curr_id, tof.cv_id, tof.dept_id \n"
                + "	 union all\n"
                + "	select tof.coa_code, tof.curr_id, tof.cv_id, tof.dept_id, "
                + "	       sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, tof.coa_code, ifnull(gl.dr_amt,0), ifnull(gl.cr_amt,0), 'DR')) dr_amt, "
                + "      sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, tof.coa_code, ifnull(gl.dr_amt,0), ifnull(gl.cr_amt,0), 'CR')) cr_amt\n"
                + "	  from tmp_op_filter tof, gl "
                + "	 where ifnull(gl.tran_source,'-') <> 'OPENING' and gl.gl_date between '" + opDate + "' \n"
                + "      and '" + tranDate + "' and (gl.dept_id = '" + dept + "' or '-' = '" + dept + "')"
                + "        and (gl.cv_id = " + cvId + " or -1 = " + cvId + ") and tof.comp_code = gl.comp_id and (tof.coa_code = gl.source_ac_id or tof.coa_code = gl.account_id) \n"
                + "        and tof.curr_id = gl.from_cur_id and tof.user_id = '" + userId
                + "' and tof.dept_id = gl.dept_id and tof.cv_id = gl.cv_id"
                + " group by tof.coa_code, tof.curr_id, tof.cv_id, tof.dept_id,gl.source_ac_id, gl.account_id) a \n"
                + "group by coa_code, curr_id, cv_id, dept_id";

        execSQL("delete from tmp_op_cl_apar where user_id = '" + userId + "'");
        execSQL(strSql);
        logger.info("tmp_op_cl_apar : End");

        /*logger.info("tmp_op_cl_apar update : Start");
        String strSql1 = "update tmp_op_cl_apar toc join ( \n" +
            "select cv_id, coa_code, curr_id, '" + userId + "' user_id , sum(dr_amt) dr_amt, sum(cr_amt) cr_amt \n" +
            "from (\n" +
            "	select tof.cv_id, tof.coa_code, tof.curr_id,\n" +
            "		   sum(ifnull(gl.dr_amt,0)) dr_amt, sum(ifnull(gl.cr_amt,0)) cr_amt\n" +
            "	  from tmp_op_filter tof, gl\n" +
            "	 where tof.comp_code = gl.comp_id and tof.curr_id = gl.from_cur_id and \n" +
            "          tof.dept_id = gl.dept_id and tof.tran_source = gl.tran_source and " +
                "      tof.cv_id = gl.cv_id and " +
            "	       tof.coa_code = gl.source_ac_id and tof.op_date = gl.gl_date and tof.user_id = '" + userId + "' " +
                       "and (gl.dept_id = '" + dept + "' or '-' = '" + dept + "')\n" +
                       " and (gl.cv_id = " + cvId + " or -1 = " + cvId + ") \n" +
                " and gl.gl_date = '" + opDate + "' " + 
            "     group by tof.coa_code, tof.curr_id \n" + 
            "	 union all\n" +
            "	select tof.cv_id, tof.coa_code, tof.curr_id, " +
            "	       get_dr_cr_amt(gl.source_ac_id, gl.account_id, tof.coa_code, ifnull(gl.dr_amt,0), ifnull(gl.cr_amt,0), 'DR') dr_amt, " +
                "      get_dr_cr_amt(gl.source_ac_id, gl.account_id, tof.coa_code, ifnull(gl.dr_amt,0), ifnull(gl.cr_amt,0), 'CR') cr_amt\n" +
            "	  from tmp_op_filter tof, gl " +
            "	 where ifnull(gl.tran_source,'-') <> 'OPENING' and gl.gl_date >= '" + opDate + "' \n" +
                    "      and gl.gl_date < '" + fromDate + "' and (gl.dept_id = '" + dept + "' or '-' = '" + dept + "')"
                + "     and tof.cv_id = gl.cv_id "
                + "        and (gl.cv_id = " + cvId + " or -1 = " + cvId + ") and tof.comp_code = gl.comp_id and (tof.coa_code = gl.source_ac_id or tof.coa_code = gl.account_id) \n" +
            "        and tof.curr_id = gl.from_cur_id and tof.user_id = '" + userId + "') a \n" +
            "group by cv_id, coa_code, curr_id) a on toc.coa_id = a.coa_code and toc.curr_id = a.curr_id and toc.user_id = a.user_id " +
                " and toc.cv_id = a.cv_id " +
            "set toc.opening = (a.dr_amt - a.cr_amt), toc.closing = (toc.dr_amt - toc.cr_amt) - (a.dr_amt - a.cr_amt) \n" +
            " where toc.user_id = '" + userId + "'";
        
        execSQL(strSql1);*/
        execSQL("delete from tmp_op_cl_apar where user_id = '" + userId + "' and dr_amt = 0 and cr_amt = 0 and ifnull(opening,0) = 0 and ifnull(closing,0) = 0");
        logger.info("tmp_op_cl_apar update : End");
    }

    @Override
    public void genArAp1(String compCode, String fromDate, String opDate,
            String tranDate, String coaCode, String currency, String dept, String cvId, String userId) throws Exception {
        logger.info("Ar / Ap Start.");
        String delSql = "delete from tmp_op_cl_apar where user_id = '" + userId + "'";
        execSQL(delSql);
        String strSql = "insert into tmp_op_cl_apar(coa_id, curr_id, user_id, cv_id, dept_id, dr_amt, cr_amt)\n"
                + "select  vcc.account_code, vcc.cur_code,'1', vcc.id ,ifnull(a.dept_code, '-') dept_id,\n"
                + "if(sum(a.dr_amt-a.cr_amt)<0, sum(a.dr_amt-a.cr_amt)*-1,0),if(sum(a.dr_amt-a.cr_amt)>0, sum(a.dr_amt-a.cr_amt),0)\n"
                + "from v_trader_curr_dept vcc left join (\n"
                + "select  coa.comp_id,coa.op_date,coa.source_acc_id, coa.cur_code, coa.cv_id, coa.dept_code, \n"
                + "sum(ifnull(coa.dr_amt,0)) dr_amt, sum(ifnull(coa.cr_amt,0)) cr_amt\n"
                + "from coa_opening coa\n"
                + "union all\n"
                + "select g.comp_id,g.gl_date,g.source_ac_id, g.from_cur_id, g.cv_id, g.dept_id,\n"
                + "sum(get_dr_cr_amt(g.source_ac_id, g.source_ac_id, g.source_ac_id, \n"
                + "ifnull(g.dr_amt,0), ifnull(g.cr_amt,0), 'DR')) dr_amt,\n"
                + "sum(get_dr_cr_amt(g.source_ac_id, g.source_ac_id, g.source_ac_id, ifnull(g.dr_amt,0), ifnull(g.cr_amt,0), 'CR')) cr_amt\n"
                + "from gl g\n"
                + "where g.gl_date <= '" + tranDate + "' \n"
                + " and (g.comp_id = 1 or '-' = '" + compCode + "') and (g.from_cur_id = '" + currency + "' "
                + "or '-' = '" + currency + "') and (g.dept_id = '" + dept + "' or '-' = '" + dept + "') "
                + "and (ifnull(g.cv_id, " + cvId + ")  = -1 or -1 = " + cvId + ")\n"
                + "group by g.comp_id,g.gl_date,g.comp_id, g.source_ac_id, g.dept_id, g.from_cur_id, g.tran_source, g.cv_id) a\n"
                + "on vcc.comp_code = a.comp_id and vcc.account_code = a.source_acc_id and vcc.cur_code = a.cur_code\n"
                + "and vcc.id = a.cv_id and vcc.dept_code = a.dept_code where  (vcc.account_code = '" + coaCode + "' "
                + "or '-' = '" + coaCode + "' or vcc.coa_parent = '-') \n"
                + "and (vcc.cur_code = '" + currency + "' or '-' = '" + currency + "') and (a.cr_amt > 0 or a.dr_amt >0)\n"
                + "group by vcc.account_code,vcc.cur_code,vcc.comp_code,vcc.id";
        execSQL(strSql);
        logger.info("Ar / Ap Finished.");
    }

    @Override
    public void genTriBalance1(String compCode, String fromDate, String opDate, String tranDate, String coaCode, String currency, String dept, String cvId, String userId) throws Exception {
        if (!cvId.equals("-1")) {
            logger.info("delete tmp op filter.");
            execSQL("delete from tmp_op_filter where user_id = '" + userId + "' and cv_id <> " + cvId);
        }
        deleteTmp(userId);
        String strSqlFilter = "insert into tmp_op_filter(comp_code, coa_code, dept_id, curr_id, tran_source, op_date, user_id)"
                + "select vcc.comp_code, vcc.coa_code, ifnull(a.dept_id, '-') dept_id, vcc.cur_code, 'OPENING' as tran_source, ifnull(a.op_date, '1900-01-01') op_date,\n"
                + "'" + userId + "' from v_coa_curr vcc left join (\n"
                + "select comp_id, source_acc_id, ifnull(dept_code, '-') as dept_id, cur_code, \n"
                + "ifnull(coa.tran_source, '-') tran_source, max(op_date) as op_date\n"
                + "from coa_opening coa\n"
                + "where coa.op_date = '" + opDate + "' and ifnull(coa.tran_source, '-') = 'OPENING'\n"
                + "and (coa.comp_id = " + compCode + " or '-' = '" + compCode + "') and "
                + "(coa.cur_code = '" + currency + "' or '-' = '" + currency + "') "
                + "and (coa.dept_code = '" + dept + "' or '-' = '" + dept + "') \n"
                + "and (ifnull(coa.cv_id, " + cvId + ")  = -1 or -1 = " + cvId + ")\n"
                + "group by coa.comp_id, coa.source_acc_id, coa.dept_code, coa.cur_code, coa.tran_source) a\n"
                + "on vcc.comp_code = a.comp_id and vcc.coa_code = a.source_acc_id and vcc.cur_code = a.cur_code\n"
                + "where vcc.level >= 3 and (vcc.coa_code = '" + coaCode + "' or '-' = '" + coaCode + "' or vcc.coa_parent = '-') "
                + "and (vcc.cur_code = '" + currency + "' or '-' = '" + currency + "')";
        execSQL(strSqlFilter);

        String strSql = "insert into tmp_op_cl(coa_id, curr_id, user_id, dr_amt, cr_amt) \n"
                + "select coa_code, curr_id, '" + userId + "', if(sum(dr_amt-cr_amt)>0, sum(dr_amt-cr_amt),0), if(sum(dr_amt-cr_amt)<0, sum(dr_amt-cr_amt)*-1,0) \n"
                + "from (\n"
                + "	select tof.coa_code, tof.curr_id,\n"
                + "		   sum(ifnull(gl.dr_amt,0)) dr_amt, sum(ifnull(gl.cr_amt,0)) cr_amt\n"
                + "	  from tmp_op_filter tof, gl\n"
                + "	 where tof.comp_code = gl.comp_id and tof.curr_id = gl.from_cur_id and \n"
                + "          tof.dept_id = gl.dept_id and tof.tran_source = gl.tran_source and "
                + "	       tof.coa_code = gl.source_ac_id and tof.op_date = gl.gl_date and tof.user_id = '" + userId + "' "
                + "and (gl.dept_id = '" + dept + "' or '-' = '" + dept + "')\n"
                + " and (gl.cv_id = " + cvId + " or -1 = " + cvId + ") \n"
                + " and ifnull(gl.tran_source, '-') = 'OPENING' and gl.gl_date = '" + opDate + "' "
                + "     group by tof.coa_code, tof.curr_id \n"
                + "	 union all\n"
                + "	select tof.coa_code, tof.curr_id, "
                + "	       sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, tof.coa_code, ifnull(gl.dr_amt,0), ifnull(gl.cr_amt,0), 'DR')) dr_amt, "
                + "      sum(get_dr_cr_amt(gl.source_ac_id, gl.account_id, tof.coa_code, ifnull(gl.dr_amt,0), ifnull(gl.cr_amt,0), 'CR')) cr_amt\n"
                + "	  from tmp_op_filter tof, gl "
                + "	 where ifnull(gl.tran_source,'-') <> 'OPENING' and gl.gl_date between '" + opDate + "' \n"
                + "      and '" + tranDate + "' and (gl.dept_id = '" + dept + "' or '-' = '" + dept + "')"
                + "        and (gl.cv_id = " + cvId + " or -1 = " + cvId + ") and tof.comp_code = gl.comp_id and (tof.coa_code = gl.source_ac_id or tof.coa_code = gl.account_id) \n"
                + "        and tof.curr_id = gl.from_cur_id and tof.user_id = '" + userId + "'"
                + " group by tof.coa_code, tof.curr_id, gl.source_ac_id, gl.account_id) a \n"
                + "group by coa_code, curr_id";

        execSQL(strSql);
        execSQL("delete from tmp_op_cl where user_id = '" + userId + "' and dr_amt = 0 and cr_amt = 0 and opening = 0 and closing = 0");
    }

}
