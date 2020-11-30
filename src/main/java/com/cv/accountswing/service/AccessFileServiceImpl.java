/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.COADao;
import com.cv.accountswing.dao.DepartmentDao;
import com.cv.accountswing.dao.FontDao;
import com.cv.accountswing.dao.GlDao;
import com.cv.accountswing.dao.TraderDao;
import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.entity.Department;
import com.cv.accountswing.entity.Font;
import com.cv.accountswing.entity.Gl;
import com.cv.accountswing.entity.Trader;
import com.cv.accountswing.util.Util1;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
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
public class AccessFileServiceImpl implements AccessFileService {

    private static final Logger logger = LoggerFactory.getLogger(AccessFileServiceImpl.class);

    @Autowired
    private GlDao glDao;
    @Autowired
    private COADao coaDao;
    @Autowired
    private TraderDao trdDao;
    @Autowired
    private FontDao fontDao;
    @Autowired
    private DepartmentDao deptDao;

    @Override
    public void uploadGL(String path, int compId) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        int ttlRec = 0;
        int insertRec = 0;

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            String dbURL = "jdbc:ucanaccess://"
                    + path;
            connection = DriverManager.getConnection(dbURL);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT GL.GL_DATE, CHART_OF_AC.ACCOUNT_CODE, "
                    + "CHART_OF_AC_1.ACCOUNT_CODE AS SOURCE_ACC, GL.DESCRIPTION, GL.SUMMARY_DESP, "
                    + "GL.REFERENCE, GL.DR_AMT, GL.CR_AMT, CUS_VEN.LONG_NAME AS CV, GL.VOU_NO, "
                    + "CUS_VEN_1.LONG_NAME AS CV1, GL.SPLIT_ID \n"
                    + "FROM (((GL INNER JOIN CHART_OF_AC ON GL.ACCOUNT_ID = CHART_OF_AC.ACCOUNT_ID) "
                    + "INNER JOIN CHART_OF_AC AS CHART_OF_AC_1 ON GL.SOURCE_ACCT_ID = CHART_OF_AC_1.ACCOUNT_ID) "
                    + "LEFT JOIN CUS_VEN ON GL.CV_ID = CUS_VEN.CV_ID) LEFT JOIN CUS_VEN AS CUS_VEN_1 "
                    + "ON GL.CV_ID_1 = CUS_VEN_1.CV_ID");

            if (resultSet != null) {
                HashMap<Integer, Integer> hmIngZgy = new HashMap();
                List<Font> listFont = fontDao.getAll();
                for (Font f : listFont) {
                    hmIngZgy.put(f.getIntegraKeyCode(), f.getKey().getZawgyiKeyCode());
                }

                while (resultSet.next()) {
                    try {
                        //gl_date
                        Gl gl = new Gl();
                        gl.setGlDate(resultSet.getDate(1));
                        gl.setCompId(compId);
                        gl.setFromCurId("MMK");

                        //account
                        List<ChartOfAccount> listCOA = coaDao.search("-", "-",
                                "-", "-", "-", "-", resultSet.getString(2));
                        if (listCOA == null) {
                            throw new Exception("Invalid account id : " + resultSet.getString(2));
                        } else if (listCOA.isEmpty()) {
                            throw new Exception("Invalid account id : " + resultSet.getString(2));
                        } else {
                            ChartOfAccount coa = listCOA.get(0);
                            gl.setAccountId(coa.getCode());
                        }

                        //source account
                        listCOA = coaDao.search("-", "-", "-", "-", "-", "-", resultSet.getString(3));
                        if (listCOA == null) {
                            throw new Exception("Invalid source account id : " + resultSet.getString(3));
                        } else if (listCOA.isEmpty()) {
                            throw new Exception("Invalid source account id : " + resultSet.getString(3));
                        } else {
                            ChartOfAccount coa = listCOA.get(0);
                            gl.setSourceAcId(coa.getCode());
                        }

                        //description
                        String toFind = "\\\\";
                        String toReplace = "/";
                        String[] desp = resultSet.getString(4).replace(toFind, toReplace).split(toReplace);
                        if (desp.length > 1) {
                            gl.setDescription(desp[1]);
                        }

                        //From-naration
                        String[] from_naration = resultSet.getString(5).split("/");
                        if (from_naration.length == 2) {
                            gl.setFromDesp(Util1.getZawgyiText(hmIngZgy, from_naration[0]));
                            gl.setNaration(Util1.getZawgyiText(hmIngZgy, from_naration[1]));
                        }

                        //dr amount
                        Double drAmt = resultSet.getDouble(7);
                        gl.setDrAmt(drAmt);

                        //cr amount
                        Double crAmt = resultSet.getDouble(8);
                        gl.setCrAmt(crAmt);

                        //trader id
                        String tmpData = resultSet.getString(9);
                        if (tmpData.isEmpty()) {
                            tmpData = resultSet.getString(11);
                        }
                        if (!tmpData.isEmpty()) {
                            String tmpId = tmpData.substring(0, 3);
                            if (tmpId.equals("198")) {
                                tmpId = "001";
                            }
                            List<Trader> listTrd = trdDao.searchTrader(tmpId, "-", "-", "-", "-", "-", "-");
                            if (listTrd == null) {
                                throw new Exception("Invalid trader id : " + tmpId);
                            } else if (listTrd.isEmpty()) {
                                throw new Exception("Invalid trader id : " + tmpId);
                            } else {
                                Trader trd = listTrd.get(0);
                                gl.setTraderId(Long.parseLong(trd.getId().toString()));
                            }
                        }

                        //voucher no and department
                        if (!resultSet.getString(12).isEmpty()) {
                            if (!resultSet.getString(10).isEmpty()) {
                                String vouNo = resultSet.getString(10).substring(0, 4) + "20"
                                        + resultSet.getString(10).substring(4, 10);
                                gl.setVouNo(vouNo);
                                if (resultSet.getString(10).contains("CR")) {
                                    gl.setSplitId(8);
                                } else if (resultSet.getString(10).contains("DR")) {
                                    gl.setSplitId(9);
                                }

                                String tmpDept = resultSet.getString(10).substring(0, 2);
                                List<Department> listDept = deptDao.search("-", "-", Integer.toString(compId), tmpDept, "-");
                                if (listDept == null) {
                                    throw new Exception("Invalid dept : " + tmpDept);
                                } else if (listDept.isEmpty()) {
                                    throw new Exception("Invalid dept : " + tmpDept);
                                } else {
                                    gl.setDeptId(listDept.get(0).getDeptCode());
                                }
                            }
                        } else {
                            gl.setVouNo(resultSet.getString(10));
                        }

                        glDao.save(gl);
                        insertRec++;
                    } catch (Exception ex) {
                        logger.error("uploadCSV : " + ex.getStackTrace()[0].getLineNumber() + "-" + ex);
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException ex) {
            logger.error("uploadGL : " + ex.getMessage());
        } finally {
            // Step 3: Closing database connection
            try {
                if (null != connection) {
                    // cleanup resources, once after processing
                    if (resultSet != null) {
                        resultSet.close();
                    }
                    if (statement != null) {
                        statement.close();
                    }
                    // and then finally close connection
                    connection.close();
                }
            } catch (SQLException sqlex) {
                logger.error(sqlex.getMessage());
            }
        }

        logger.info("Total Recrods : " + ttlRec + " Total Inserted : " + insertRec);
    }

    @Override
    public void uploadTrader(String path, int compId) {

    }

    @Override
    public void uploadOpening(String path, int compId) {

    }
}
