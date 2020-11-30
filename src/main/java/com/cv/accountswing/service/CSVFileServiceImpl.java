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
import com.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.FileReader;
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
public class CSVFileServiceImpl implements CSVFileService {

    private static final Logger logger = LoggerFactory.getLogger(CSVFileServiceImpl.class);

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
    public void uploadCSVVoucher(String path, int compId) throws Exception {
        FileReader fr = new FileReader(path);
        BufferedReader reader = new BufferedReader(fr);
        CSVReader csvReader = new CSVReader(reader);
        String[] nextRecord;
        int ttlRec = 0;
        int insertRec = 0;

        HashMap<Integer, Integer> hmIngZgy = new HashMap();
        List<Font> listFont = fontDao.getAll();
        for (Font f : listFont) {
            hmIngZgy.put(f.getIntegraKeyCode(), f.getKey().getZawgyiKeyCode());
        }

        while ((nextRecord = csvReader.readNext()) != null) {
            ttlRec++;

            if (ttlRec > 1) {
                try {
                    //gl_date
                    Gl gl = new Gl();
                    gl.setGlDate(Util1.toDate(nextRecord[0], "MM/dd/yyyy"));
                    gl.setCompId(compId);
                    gl.setFromCurId("MMK");

                    //account
                    List<ChartOfAccount> listCOA = coaDao.search("-", "-", "-", "-", "-", "-", nextRecord[1]);
                    if (listCOA == null) {
                        //throw new Exception("Invalid account id : " + nextRecord[1]);
                        logger.error("Invalid account id : " + nextRecord[1]);
                    } else if (listCOA.isEmpty()) {
                        //throw new Exception("Invalid account id : " + nextRecord[1]);
                        logger.error("Invalid account id : " + nextRecord[1]);
                    } else {
                        ChartOfAccount coa = listCOA.get(0);
                        gl.setAccountId(coa.getCode());
                    }

                    //source account
                    listCOA = coaDao.search("-", "-", "-", "-", "-", "-", nextRecord[2]);
                    if (listCOA == null) {
                        //throw new Exception("Invalid source account id : " + nextRecord[2]);
                        logger.error("Invalid source account id : " + nextRecord[2]);
                    } else if (listCOA.isEmpty()) {
                        logger.error("Invalid source account id : " + nextRecord[2]);
                    } else {
                        ChartOfAccount coa = listCOA.get(0);
                        gl.setSourceAcId(coa.getCode());
                    }

                    //description
                    String toFind = "\\\\";
                    String toReplace = "/";
                    String[] desp = nextRecord[3].replace(toFind, toReplace).split(toReplace);
                    if (desp.length > 1) {
                        gl.setDescription(desp[1]);
                    }

                    //From-naration
                    String[] from_naration = nextRecord[4].split("/");
                    if (from_naration.length == 2) {
                        gl.setFromDesp(getZawgyiText(hmIngZgy, from_naration[0]));
                        gl.setNaration(getZawgyiText(hmIngZgy, from_naration[1]));
                    }

                    //dr amount
                    Double drAmt = Util1.nullZero(nextRecord[6]);
                    gl.setDrAmt(drAmt);

                    //cr amount
                    Double crAmt = Util1.nullZero(nextRecord[7]);
                    gl.setCrAmt(crAmt);

                    //trader id
                    String tmpData = nextRecord[8];
                    if (tmpData.isEmpty()) {
                        tmpData = nextRecord[10];
                    }
                    if (!tmpData.isEmpty()) {
                        String tmpId = tmpData.substring(0, 3);
                        if (tmpId.equals("198")) {
                            tmpId = "001";
                        }
                        List<Trader> listTrd = trdDao.searchTrader(tmpId, "-", "-", "-", "-", "-", "-");
                        if (listTrd == null) {
                            //throw new Exception("Invalid trader id : " + tmpId);
                            logger.error("Invalid trader id : " + tmpId);
                        } else if (listTrd.isEmpty()) {
                            //throw new Exception("Invalid trader id : " + tmpId);
                            logger.error("Invalid trader id : " + tmpId);
                        } else {
                            Trader trd = listTrd.get(0);
                            gl.setTraderId(Long.parseLong(trd.getId().toString()));
                        }
                    }

                    //voucher no and department
                    if (!nextRecord[11].isEmpty()) {
                        if (!nextRecord[9].isEmpty()) {
                            String vouNo = nextRecord[9].substring(0, 4) + "20" + nextRecord[9].substring(4, 10);
                            gl.setVouNo(vouNo);
                            if (nextRecord[9].contains("CR")) {
                                gl.setSplitId(8);
                            } else if (nextRecord[9].contains("DR")) {
                                gl.setSplitId(9);
                            }

                            String tmpDept = nextRecord[9].substring(0, 2);
                            List<Department> listDept = deptDao.search("-", "-", Integer.toString(compId), tmpDept, "-");
                            if (listDept == null) {
                                //throw new Exception("Invalid dept : " + tmpDept);
                                logger.error("Invalid dept : " + tmpDept);
                            } else if (listDept.isEmpty()) {
                                //throw new Exception("Invalid dept : " + tmpDept);
                                logger.error("Invalid dept : " + tmpDept);
                            } else {
                                gl.setDeptId(listDept.get(0).getDeptCode());
                            }
                        }
                    } else {
                        gl.setVouNo(nextRecord[9]);
                    }

                    glDao.save(gl);
                    insertRec++;
                } catch (Exception ex) {
                    logger.error("uploadCSV : " + ex.getStackTrace()[0].getLineNumber() + "-" + ex);
                }
            }
        }

        logger.info("Total Recrods : " + ttlRec + " Total Inserted : " + insertRec);
    }

    private String getZawgyiText(HashMap<Integer, Integer> hmIngZgy, String text) {
        String tmpStr = "";

        if (text != null) {
            for (int i = 0; i < text.length(); i++) {
                String tmpS = Character.toString(text.charAt(i));
                int tmpChar = (int) text.charAt(i);

                if (hmIngZgy.containsKey(tmpChar)) {
                    char tmpc = (char) hmIngZgy.get(tmpChar).intValue();
                    if (tmpStr.isEmpty()) {
                        tmpStr = Character.toString(tmpc);
                    } else {
                        tmpStr = tmpStr + Character.toString(tmpc);
                    }
                } else if (tmpS.equals("ƒ")) {
                    if (tmpStr.isEmpty()) {
                        tmpStr = "ႏ";
                    } else {
                        tmpStr = tmpStr + "ႏ";
                    }
                } else if (tmpStr.isEmpty()) {
                    tmpStr = tmpS;
                } else {
                    tmpStr = tmpStr + tmpS;
                }
            }
        }

        return tmpStr;
    }
}
