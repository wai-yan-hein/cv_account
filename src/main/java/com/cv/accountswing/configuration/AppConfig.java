package com.cv.accountswing.configuration;

import com.cv.accountswing.ui.cash.AllCash;
import com.cv.accountswing.ui.cash.SalePurchaseBook;
import com.cv.accountswing.ui.cash.common.AllCashTableModel;
import com.cv.accountswing.ui.cash.common.SalePurchaseTableModel;
import com.cv.accountswing.ui.filter.FilterPanel;
import com.cv.accountswing.ui.journal.CrDrVoucher;
import com.cv.accountswing.ui.journal.CrDrVoucherEntry;
import com.cv.accountswing.ui.journal.common.CrDrVoucherEntryTableModel;
import com.cv.accountswing.ui.journal.common.CrDrVoucherTableModel;
import com.cv.accountswing.ui.report.AparGlReport;
import java.util.Properties;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
/*@ComponentScan({
    "com.cv.accounts.accountswing.configuration",
    "com.cv.accounts.accountswing.messaging",
    "com.cv.accounts.dao",
    "com.cv.accounts.service",
    "com.cv.accounts.accountswing.ui"
})*/
@ComponentScan(basePackages = "com.cv.accountswing")
//@Import({MessagingConfiguration.class, MessagingListnerConfiguration.class})
public class AppConfig {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public AllCash allCash() {
        return new AllCash();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public SalePurchaseBook salePurchaseBook() {
        return new SalePurchaseBook();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public SalePurchaseTableModel salePurchaseTableModel() {
        return new SalePurchaseTableModel();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public FilterPanel filterPanel() {
        return new FilterPanel();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public AllCashTableModel allCashTableModel() {
        return new AllCashTableModel();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public CrDrVoucher crDrVoucher() {
        return new CrDrVoucher();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public CrDrVoucherEntry crDrVoucherEntry() {
        return new CrDrVoucherEntry();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public CrDrVoucherTableModel crDrVoucherTableModel() {
        return new CrDrVoucherTableModel();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public CrDrVoucherEntryTableModel crDrVoucherEntryTableModel() {
        return new CrDrVoucherEntryTableModel();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public AparGlReport aparGlReport() {
        return new AparGlReport();
    }

    public AppConfig() {
        super();
        Properties props = new Properties();

        //Console log
        /* props.put("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
        props.put("log4j.appender.stdout.Target", "System.out");
        props.put("log4j.rootLogger", "ERROR, stdout");
        props.put("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
        props.put("log4j.appender.stdout.layout.ConversionPattern", "%d{dd MMM yyyy HH:mm:ss,SSS} %-5p: %c - %m%n");
        props.put("log4j.logger.org.springframework", "ERROR");
        props.put("log4j.logger.com.cv.accounts.accountswing", "INFO");*/
        //File log
        props.put("log4j.rootLogger", "ERROR, FILE");
        props.put("log4j.appender.FILE", "org.apache.log4j.FileAppender");
        props.put("log4j.appender.FILE.File", "account-swing.log");
        props.put("log4j.appender.FILE.ImmediateFlush", true);
        props.put("og4j.appender.FILE.Threshold", "ERROR");
        props.put("log4j.appender.FILE.Append", true);
        props.put("og4j.appender.FILE.MaxFileSize", "5KB");
        props.put("log4j.appender.FILE.layout", "org.apache.log4j.PatternLayout");
        props.put("log4j.appender.FILE.layout.conversionPattern", "%d{dd MMM yyyy HH:mm:ss,SSS} %-5p: %c - %m%n");
        props.put("og4j.appender.FILE.Threshold", "ERROR");
        props.put("log4j.logger.org.springframework", "ERROR");
        props.put("log4j.logger.com.cv.accountswing", "ERROR");
        PropertyConfigurator.configure(props);
    }

}
