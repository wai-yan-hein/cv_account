/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author Lenovo
 */
@Service
public class FireBaseConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(FireBaseConfig.class);

    @PostConstruct
    public void initialize() {
        List<FirebaseApp> apps = FirebaseApp.getApps();
        if (apps == null) {
            LOGGER.info("Firebase Intializing.");
            try {
                InputStream serviceAccount = this.getClass().getResourceAsStream("/firebase/fb-sdk.json");
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setDatabaseUrl("https://cv-account.firebaseio.com")
                        .build();
                FirebaseApp.initializeApp(options);
            } catch (IOException e) {
                LOGGER.error("FBIntialize :" + e.getMessage());
            }
        }

    }
}
