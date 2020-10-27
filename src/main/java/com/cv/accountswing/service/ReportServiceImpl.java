/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.COADao;
import com.cv.accountswing.dao.ReportDao;
import com.cv.accountswing.dao.TmpProfitAndLostDao;
import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.entity.helper.ProfitAndLostRetObj;
import com.cv.accountswing.entity.temp.TmpProfitAndLost;
import com.cv.accountswing.util.Util1;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author winswe
 */
@Service
@Transactional
public class ReportServiceImpl implements ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    @Autowired
    private ReportDao dao;
    @Autowired
    private COADao coaDao;
    @Autowired
    private TmpProfitAndLostDao tapDao;

    @Override
    public void genCreditVoucher(String reportPath, String filePath, String fontPath,
            Map<String, Object> parameters) throws Exception {
        dao.genReport(reportPath, filePath, fontPath, parameters);
    }

    @Override
    public void getProfitLost(String plProcess, String from, String to, String dept,
            String currency, String comp, String userId) throws Exception {
        dao.execSQLRpt("delete from tmp_profit_lost where user_id = '" + userId + "' and comp_id = " + comp);
        String strInsert = "insert into tmp_profit_lost(group_desp, acc_id, acc_name, curr_id, "
                + "acc_total, user_id, comp_id, sort_order)";
        String[] process = plProcess.split(",");
        int sortOrder = 1;

        from = Util1.toDateStrMYSQL(from, "dd/MM/yyyy");
        to = Util1.toDateStrMYSQL(to, "dd/MM/yyyy");
        String tmpFrom = Util1.addDateTo(from, -1);
        tmpFrom = Util1.toDateStrMYSQL(tmpFrom, "dd/MM/yyyy");
        //Sales Income
        for (String tmp : process) {
            switch (tmp) {
                case "os":
                    String strOS = "select 'Cost of Sale', 'os', 'Opening Stock', curr_code, sum(ifnull(amount,0)) amount, "
                            + "'" + userId + "',comp_id, " + sortOrder + "\n"
                            + "from stock_op_value\n"
                            + "where date(tran_date) = '" + tmpFrom + "' and comp_id = " + comp
                            + " and (dept_code = '" + dept + "' or '-' = '" + dept + "') \n"
                            + "and curr_code = '" + currency + "' \n"
                            + "group by comp_id, curr_code";
                    //logger.info("os sql : " + strOS);
                    dao.execSQLRpt(strInsert + "\n" + strOS);
                    break;
                case "cs":
                    String strCS = "select 'Cost of Sale', 'cs', 'Closing Stock', curr_code, sum(ifnull(amount,0)*-1) amount, "
                            + "'" + userId + "',comp_id, " + sortOrder + "\n"
                            + "from stock_op_value\n"
                            + "where date(tran_date) = '" + to + "' and comp_id = " + comp
                            + " and (dept_code = '" + dept + "' or '-' = '" + dept + "') \n"
                            + "and curr_code = '" + currency + "' \n"
                            + "group by comp_id, curr_code";
                    //logger.info("cs sql : " + strCS);
                    dao.execSQLRpt(strInsert + "\n" + strCS);
                    break;
                default:
                    List<ChartOfAccount> listCOA = coaDao.getAllChild(tmp, comp);

                    for (ChartOfAccount coa : listCOA) {
                        String coaCode = coa.getCode();
                        String accDesp = coa.getCoaNameEng();
                        String group = coa.getParentUsrDesp();
                        /*String strSelectDr = "select '" + group + "' pl_group, a.acc_id,'" + accDesp + "',a.curr_id"
                                + ", sum(ifnull(a.dr_amt,0) - ifnull(a.cr_amt,0)) acc_total,'" + userId + "' user_id \n"
                                + ", " + comp + "," + sortOrder + "\n "
                                + "from (\n select '" + coaCode + "' acc_id, "
                                + "get_dr_cr_amt(source_ac_id, account_id, '" + coaCode + "', dr_amt, cr_amt, 'DR') as dr_amt,"
                                + "get_dr_cr_amt(source_ac_id, account_id, '" + coaCode + "', dr_amt, cr_amt, 'CR') as cr_amt,\n"
                                + "from_cur_id curr_id\n"
                                + "from gl\n"
                                + "where gl_date between '" + from
                                + "' and '" + to + "' \n"
                                + "and (dept_id = '" + dept + "' or '-' = '" + dept + "')\n"
                                + "and from_cur_id = '" + currency + "'\n"
                                + "and (source_ac_id = '" + coaCode + "')\n"
                                + "and ifnull(tran_source,'-') <> 'OPENING') a\n"
                                + "group by a.acc_id, a.curr_id";*/
 /*String strSelectDr = "select '" + group + "' pl_group, a.acc_id,'" + accDesp + "',a.curr_id"
                                + ", sum(ifnull(a.dr_amt,0) - ifnull(a.cr_amt,0)) acc_total,'" + userId + "' user_id \n"
                                + ", " + comp + "," + sortOrder + "\n "
                                + "from (\n select '" + coaCode + "' acc_id, "
                                + "dr_amt,"
                                + "cr_amt,\n"
                                + "from_cur_id curr_id\n"
                                + "from gl\n"
                                + "where gl_date between '" + from
                                + "' and '" + to + "' \n"
                                + "and (dept_id = '" + dept + "' or '-' = '" + dept + "')\n"
                                + "and from_cur_id = '" + currency + "'\n"
                                + "and (source_ac_id = '" + coaCode + "')\n"
                                + "and ifnull(tran_source,'-') <> 'OPENING') a\n"
                                + "group by a.acc_id, a.curr_id";*/
                        String strSelectDr = "select '" + group + "' pl_group, a.acc_id,'" + accDesp + "',a.curr_id"
                                + ", sum(ifnull(a.dr_amt,0) - ifnull(a.cr_amt,0)) acc_total,'" + userId + "' user_id \n"
                                + ", " + comp + "," + sortOrder + "\n "
                                + "from (\n select '" + coaCode + "' acc_id, "
                                + "if(tran_source='GV',cr_amt,dr_amt) dr_amt,"
                                + "if(tran_source='GV', dr_amt, cr_amt) cr_amt,\n"
                                + "from_cur_id curr_id\n"
                                + "from gl\n"
                                + "where gl_date between '" + from
                                + "' and '" + to + "' \n"
                                + "and (dept_id = '" + dept + "' or '-' = '" + dept + "')\n"
                                + "and from_cur_id = '" + currency + "'\n"
                                + "and (source_ac_id = '" + coaCode + "')\n"
                                + "and ifnull(tran_source,'-') <> 'OPENING') a\n"
                                + "group by a.acc_id, a.curr_id";

                        logger.info("sql : " + strSelectDr);
                        dao.execSQLRpt(strInsert + "\n" + strSelectDr);

                        String strSelectCr = "select '" + group + "' pl_group, a.acc_id,'" + accDesp + "',a.curr_id"
                                + ", sum(ifnull(a.cr_amt,0) - ifnull(a.dr_amt,0)) acc_total,'" + userId + "' user_id \n"
                                + ", " + comp + "," + sortOrder + "\n "
                                + "from (\n select '" + coaCode + "' acc_id, "
                                + "get_dr_cr_amt(source_ac_id, account_id, '" + coaCode + "', dr_amt, cr_amt, 'DR') as dr_amt, "
                                + "get_dr_cr_amt(source_ac_id, account_id, '" + coaCode + "', dr_amt, cr_amt, 'CR') as cr_amt,\n"
                                + "from_cur_id curr_id\n"
                                + "from gl\n"
                                + "where gl_date between '" + from
                                + "' and '" + to + "' \n"
                                + "and (dept_id = '" + dept + "' or '-' = '" + dept + "')\n"
                                + "and from_cur_id = '" + currency + "'\n"
                                + "and (account_id = '" + coaCode + "')\n"
                                + "and ifnull(tran_source,'-') <> 'OPENING') a\n"
                                + "group by a.acc_id, a.curr_id";
                        /*String strSelectCr = "select '" + group + "' pl_group, a.acc_id,'" + accDesp + "',a.curr_id"
                                + ", sum(ifnull(a.dr_amt,0) - ifnull(a.cr_amt,0)) acc_total,'" + userId + "' user_id \n"
                                + ", " + comp + "," + sortOrder + "\n "
                                + "from (\n select '" + coaCode + "' acc_id, "
                                + "if(tran_source = 'GV', cr_amt, dr_amt) dr_amt, "
                                + "if(tran_source = 'GV', dr_amt, cr_amt) cr_amt,\n"
                                + "from_cur_id curr_id\n"
                                + "from gl\n"
                                + "where gl_date between '" + from
                                + "' and '" + to + "' \n"
                                + "and (dept_id = '" + dept + "' or '-' = '" + dept + "')\n"
                                + "and from_cur_id = '" + currency + "'\n"
                                + "and (account_id = '" + coaCode + "')\n"
                                + "and ifnull(tran_source,'-') <> 'OPENING') a\n"
                                + "group by a.acc_id, a.curr_id";*/

                        logger.info("sql : " + strSelectCr);
                        dao.execSQLRpt(strInsert + "\n" + strSelectCr);
                    }
                    break;
            }

            sortOrder++;
        }

        /*dao.execSQLRpt("update tmp_profit_lost set acc_total = acc_total * -1\n" +
            "where user_id = '" + userId + "' and comp_id = " + comp + " and acc_total < 0;");*/
    }

    @Override
    public ProfitAndLostRetObj getPLCalculateValue(String userId, String compId) {
        ProfitAndLostRetObj obj = new ProfitAndLostRetObj();
        List<TmpProfitAndLost> listTPAL = tapDao.search(userId, compId);

        for (TmpProfitAndLost tpal : listTPAL) {
            switch (tpal.getSortOrder()) {
                case 1: //Sales Income
                    obj.addSaleIncome(tpal.getAccTotal());
                    break;
                case 2: //Opening Stock
                    obj.addOPStock(tpal.getAccTotal());
                    break;
                case 3: //Purchase
                    obj.addPurchase(tpal.getAccTotal());
                    break;
                case 4: //Closing Stock
                    obj.addCLStock(tpal.getAccTotal());
                    break;
                case 5: //Other Income
                    obj.addOtherIncome(tpal.getAccTotal());
                    break;
                case 6: //Other Expense
                    obj.addOtherExpense(tpal.getAccTotal());
                    break;
            }
        }

        return obj;
    }

    @Override
    public void genGLReport(String from, String to, String sourceAcId, String acId, String compId,
            String desp, String fromCurr, String toCurr, String ref, String dept, String tranSource,
            String vouNo, String cvId, String userId, String glVouNo, String deptName, String traderName) {
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
                strFilter = "o.description like '" + desp + "'";
            } else {
                strFilter = strFilter + " and o.description like '" + desp + "'";
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

        /*if(!acId.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.accountId = '" + acId + "'";
            }else{
                strFilter = strFilter + " and o.accountId = '" + acId + "'";
            }
        }*/
        if (!fromCurr.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.fromCurId = '" + fromCurr + "'";
            } else {
                strFilter = strFilter + " and o.fromCurId = '" + fromCurr + "'";
            }
        }

        if (!toCurr.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.toCurId = '" + toCurr + "'";
            } else {
                strFilter = strFilter + " and o.toCurId = '" + toCurr + "'";
            }
        }

        if (!ref.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.reference like '%" + ref + "%'";
            } else {
                strFilter = strFilter + " and o.reference like '%" + ref + "%'";
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

        if (!compId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.compId = " + compId;
            } else {
                strFilter = strFilter + " and o.compId = " + compId;
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
    }

    @Override
    public void genBalanceSheet(String from, String to, String dept, String userId,
            String compId, String curr) throws Exception {
        String strSqlDelete = "delete from tmp_balance_sheet where user_id = '" + userId + "'";
        dao.execSQLRpt(strSqlDelete);

        //from = Util1.toDateStrMYSQL(from, "EE MMM d y H:m:s 'GMT'Z (zz)");
        to = Util1.toDateStrMYSQL(to, "dd/MM/yyyy");

        String strSql = "insert tmp_balance_sheet(bs_side,coa_code,bs_balance,user_id)\n"
                + "select bs_side, child_coa_code, sum((dr_amt-cr_amt)*bs_factor) bs_balance,'"
                + userId + "' "
                + "from v_balance_sheet_detail "
                + "where comp_id = " + compId + " and dept_id = '" + dept + "' "
                + " and gl_date between '" + from
                + "' and '" + to + "' and from_cur_id = '"
                + curr + "' "
                + "group by bs_side, child_coa_code";
        dao.execSQLRpt(strSql);
    }
}
