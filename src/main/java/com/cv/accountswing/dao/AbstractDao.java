package com.cv.accountswing.dao;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.view.JasperViewer;

import org.hibernate.Criteria;
import org.hibernate.Filter;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractDao<PK extends Serializable, T> {
    
    private static final Logger logger = LoggerFactory.getLogger(AbstractDao.class);
    private final Class<T> persistentClass;
    
    @SuppressWarnings("unchecked")
    public AbstractDao() {
        this.persistentClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }
    
    @Autowired
    private SessionFactory sessionFactory;
    
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }
    
    @SuppressWarnings("unchecked")
    public T getByKey(PK key) {
        return (T) getSession().get(persistentClass, key);
    }
    
    public void persist(T entity) {
        try {
            getSession().saveOrUpdate(entity);
        } catch (Exception e) {
            logger.error("persiste  :" + e.getMessage());
        }
    }
    
    public void delete(T entity) {
        getSession().delete(entity);
    }
    
    protected Criteria createEntityCriteria() {
        return getSession().createCriteria(persistentClass);
    }
    
    public List<T> findHSQL(String hsql) {
        List<T> list = null;
        try {
            Query query = getSession().createQuery(hsql);
            list = query.list();
        } catch (Exception e) {
            logger.error("findHSQL  :" + e.getMessage());
        }
        return list;
        
    }
    
    public List findHSQLPC(String hsql, String filterName, String paramName, String paramValue) {
        Session session = getSession();
        Filter filter = session.enableFilter(filterName);
        filter.setParameter(paramName, paramValue);
        Query query = session.createQuery(hsql);
        List list = query.list();
        session.disableFilter(filterName);
        return list;
    }
    
    public List findHSQLList(String hsql) {
        Query query = getSession().createQuery(hsql);
        List list = query.list();
        return list;
    }
    
    public int execUpdateOrDelete(String hsql) {
        Query query = getSession().createQuery(hsql);
        int cnt = query.executeUpdate();
        return cnt;
    }
    
    public Object exeSQL(String hsql) {
        Query query = getSession().createQuery(hsql);
        Object obj = query.uniqueResult();
        return obj;
        
    }
    
    public Object findByKey(Class type, Serializable id) {
        Object obj = null;
        
        try {
            if (!id.equals("")) {
                
                obj = getSession().get(type, id);
                //tran.commit();
            }
        } catch (Exception ex) {
            logger.error("find1 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
        return obj;
        
    }
    
    public List<T> saveBatch(List<T> list) {
        for (T obj : list) {
            persist(obj);
        }
        return list;
    }
    
    public void execProc(String procName, String... parameters) {
        String strSQL = "{call " + procName + "(";
        String tmpStr = "";
        
        for (int i = 0; i < parameters.length; i++) {
            if (tmpStr.isEmpty()) {
                tmpStr = "?";
            } else {
                tmpStr = tmpStr + ",?";
            }
        }
        
        strSQL = strSQL + tmpStr + ")}";
        
        Query query = getSession().createSQLQuery(strSQL);
        int i = 0;
        for (String prm : parameters) {
            query.setParameter(i, prm);
            i++;
        }
        
        query.executeUpdate();
    }
    
    public void execSQL(String... strSql) throws Exception {
        for (String sql : strSql) {
            SQLQuery query = getSession().createSQLQuery(sql);
            query.executeUpdate();
        }
    }
    
    public void execSQL(List<String> listSql) throws Exception {
        listSql.stream().map(sql -> getSession().createSQLQuery(sql)).forEachOrdered(query -> {
            query.executeUpdate();
        });
    }
    
    public String getGlLogSql(long glId, String actionType) {
        String strSql = "insert into gl_log (gl_id,gl_date,created_date,modify_date,modify_by,"
                + "description,source_ac_id,account_id,to_cur_id,from_cur_id,ex_rate,dr_amt,cr_amt,"
                + "reference,dept_id,voucher_no,user_id,cv_id,cheque_no,comp_code,gst,tran_source,"
                + "action_type,action_dt,split_id,remark,from_desp,to_desp,naration,"
                + "project_id) "
                + "select gl_id,gl_date,created_date,modify_date,modify_by,description,source_ac_id,"
                + "account_id,to_cur_id,from_cur_id,ex_rate,dr_amt,cr_amt,reference,dept_id,"
                + "voucher_no,user_id,cv_id,cheque_no,comp_id,gst,tran_source,'" + actionType
                + "',sysdate(),split_id,remark,from_"
                + "desp,to_desp,naration,project_id from gl where gl_id = " + glId;
        return strSql;
    }
    
    public Object getAggregate(String sql) {
        SQLQuery query = getSession().createSQLQuery(sql);
        Object obj = query.uniqueResult();
        return obj;
    }
    
    public void doWork(Work work) {
        Session sess = getSession();
        sess.doWork(work);
    }
    
    public void doReportPDF(final String reportPath, final String filePath,
            final Map<String, Object> parameters, final String fontPath) throws Exception {
        Work work = (Connection con) -> {
            try {
                parameters.put("REPORT_CONNECTION", con);
                JasperPrint jp = getReport(reportPath, parameters, con, fontPath);
                JasperViewer.viewReport(jp, false);
            } catch (Exception ex) {
                logger.error("doReportPDF : " + ex);
            }
        };
        
        doWork(work);
    }
    
    private JasperPrint getReport(String reportPath, Map<String, Object> parameters,
            Connection con, String fontPath) throws Exception {
        JasperPrint jp;
        
        reportPath = reportPath + ".jasper";
        JasperReportsContext jasperReportsContext = DefaultJasperReportsContext.getInstance();
        jasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.font.name", fontPath);
        jasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.encoding", "Identity-H");
        jasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.embedded", "true");
        //JRProperties.setProperty("net.sf.jasperreports.default.pdf.font.name", fontPath);
        //JRProperties.setProperty("net.sf.jasperreports.default.pdf.encoding", "Identity-H");
        //JRProperties.setProperty("net.sf.jasperreports.default.pdf.embedded", true);
        jp = JasperFillManager.fillReport(reportPath, parameters, con);
        
        return jp;
    }
    
    private ByteArrayOutputStream exportPDF(JasperPrint jp) throws Exception {
        JRPdfExporter exporter = new JRPdfExporter();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
        exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
        
        exporter.exportReport();
        return baos;
    }
    
    private void writeToFile(String path, ByteArrayOutputStream baos) {
        
        try (OutputStream outputStream = new FileOutputStream(path)) {
            baos.writeTo(outputStream);
        } catch (Exception ex) {
            logger.error("writeToFile : " + ex);
        } finally {
            try {
                baos.close();
            } catch (IOException ex) {
                
            }
        }
    }
}
