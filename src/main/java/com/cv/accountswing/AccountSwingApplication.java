package com.cv.accountswing;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.LoginDialog;
import com.formdev.flatlaf.FlatLightLaf;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.TimeZone;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AccountSwingApplication {

    private static final Logger LOGGER = Logger.getLogger(AccountSwingApplication.class);
    private static final SplashWindow SPLASH_WINDOW = new SplashWindow();
    private static ConfigurableApplicationContext context;
    private static final ImageIcon accIcon = new ImageIcon(AccountSwingApplication.class.getResource("/images/male_user_26px.png"));

    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("TimeZone"));
        SPLASH_WINDOW.run();
    }

    public static void main(String[] args) {
        LOGGER.info("MAIN STARTED...");
        try {
            /*IntelliJTheme.install(AccountSwingApplication.class.getResourceAsStream(
            "/theme/light_theme.json"));
            JDialog.setDefaultLookAndFeelDecorated(true);*/

            UIManager.setLookAndFeel(new FlatLightLaf());
            System.setProperty("flatlaf.useWindowDecorations", "false");
            System.setProperty("flatlaf.menuBarEmbedded", "false");
            System.setProperty("flatlaf.animation", "true");
            System.setProperty("flatlaf.uiScale.enabled", "true");

            /*UIManager.getDefaults().entrySet().stream().sorted((o1, o2) -> {
            return o1.getKey().toString().compareTo(o2.getKey().toString());
            }).forEach(entry -> {
            LOGGER.info("Key :" + entry.getKey().toString() + "---" + "Value" + gson.toJson(entry.getValue()));
            });*/
        } catch (UnsupportedLookAndFeelException ex) {
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
            loginDialog.setIconImage(accIcon.getImage());
            loginDialog.checkMachineRegister();
            loginDialog.setLocationRelativeTo(null);
            loginDialog.setVisible(true);
            if (loginDialog.isLogin()) {
                ApplicationMainFrame appMain = context.getBean(ApplicationMainFrame.class);
                java.awt.EventQueue.invokeLater(() -> {
                    appMain.companyUserRoleAssign();
                    appMain.loadSysProperties();
                    appMain.startNetworkDetector();
                    appMain.setIconImage(new ImageIcon(AccountSwingApplication.class.getResource("/images/logo.png")).getImage());
                    appMain.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    appMain.setVisible(true);
                });

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
