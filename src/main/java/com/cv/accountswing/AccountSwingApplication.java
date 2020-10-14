package com.cv.accountswing;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.LoginDialog;
import com.cv.accountswing.entity.view.VUsrCompAssign;
import com.cv.accountswing.service.UsrCompRoleService;
import com.formdev.flatlaf.IntelliJTheme;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AccountSwingApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountSwingApplication.class);
    private static final SplashWindow SPLASH_WINDOW = new SplashWindow();
    private static ConfigurableApplicationContext context;

    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("TimeZone"));
        SPLASH_WINDOW.showSplash();
    }

    public static void main(String[] args) {
        LOGGER.info("MAIN STARTED...");
        try {
            IntelliJTheme.install(AccountSwingApplication.class.getResourceAsStream(
                    "/theme/light_theme.json"));
            //UIManager.setLookAndFeel(new FlatLightLaf());
            /*UIManager.getDefaults().entrySet().stream().sorted((o1, o2) -> {
            return o1.getKey().toString().compareTo(o2.getKey().toString());
            }).forEach(entry -> {
            LOGGER.info("Key :" + entry.getKey().toString() + "---" + "Value" + gson.toJson(entry.getValue()));
            });*/

        } catch (Exception ex) {
            LOGGER.error("Theme Error :" + ex.getMessage());
        }

        try {
            Global.sock = new ServerSocket(10003);//Pharmacy

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(new JFrame(), "You cannot run two program at the same time in the same machine.",
                    "Duplicate Program running.", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        try {
            SpringApplicationBuilder builder = new SpringApplicationBuilder(AccountSwingApplication.class);
            builder.headless(false);
            builder.web(WebApplicationType.NONE);
            builder.bannerMode(Banner.Mode.OFF);
            context = builder.run(args);

            //For message receiver
            UUID uuid = UUID.randomUUID();
            Global.uuid = uuid.toString();
            //============================
            SPLASH_WINDOW.stopSplah();
            LoginDialog loginDialog = context.getBean(LoginDialog.class);
            loginDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    context.close();
                    System.exit(0);
                }
            });
            loginDialog.startThread();
            loginDialog.setLocationRelativeTo(null);
            loginDialog.setVisible(true);
            if (loginDialog.isLogin()) {
                UsrCompRoleService usrCompRoleService = context.getBean(UsrCompRoleService.class);
                List<VUsrCompAssign> listVUCA = usrCompRoleService.
                        getAssignCompany(Global.loginUser.getUserId().toString());

                if (listVUCA == null) {
                    JOptionPane.showMessageDialog(new JFrame(),
                            "No company assign to the user",
                            "Invalid Compay Access", JOptionPane.ERROR_MESSAGE);
                    System.exit(-1);
                } else if (listVUCA.isEmpty()) {
                    JOptionPane.showMessageDialog(new JFrame(),
                            "No company assign to the user",
                            "Invalid Compay Access", JOptionPane.ERROR_MESSAGE);
                    System.exit(-1);
                } else if (listVUCA.size() > 1) {

                } else {
                    Global.roleId = listVUCA.get(0).getKey().getRoleId();
                    Global.compId = listVUCA.get(0).getKey().getCompCode();
                    Global.companyName = listVUCA.get(0).getCompName();

                    ApplicationMainFrame appMain = context.getBean(ApplicationMainFrame.class);
                    java.awt.EventQueue.invokeLater(() -> {
                        //appMain.initGlobalData();
                        //appMain.initProperty();
                        //appMain.autoSyncStart();
                        appMain.setExtendedState(JFrame.MAXIMIZED_BOTH);
                        appMain.setVisible(true);
                    });
                }
            } else {
                context.close();
                System.exit(0);
            }
        } catch (BeansException ex) {
            SPLASH_WINDOW.stopSplah();
            LOGGER.error("main : " + ex.getMessage());
        }

        //SpringApplication.run(AccountSwingApplication.class, args);
    }

    public static void restart() {
        ApplicationArguments args = context.getBean(ApplicationArguments.class);

        Thread thread = new Thread(() -> {
            context.close();
            main(args.getSourceArgs());
        });

        thread.setDaemon(false);
        thread.start();
    }

}
