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
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
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
    }

}
