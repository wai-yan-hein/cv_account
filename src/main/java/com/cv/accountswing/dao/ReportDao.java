/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import java.util.Map;

/**
 *
 * @author winswe
 */
public interface ReportDao{
    public void genReport(String reportPath, String filePath, String fontPath, 
            Map<String, Object> parameters) throws Exception;
    public void execSQLRpt(String... strSql) throws Exception;
}
