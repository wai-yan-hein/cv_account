/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.common.FontConventor;
import com.cv.accountswing.entity.view.VApar;
import com.cv.accountswing.ui.report.AparGlReport;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author Lenovo
 */
@Service
public class FirebaseServieImpl implements FirebaseService {

    private final Logger LOGGER = LoggerFactory.getLogger(AparGlReport.class);
    HashMap<String, VApar> hmApar = new HashMap<>();
    Gson gson = new Gson();

    @Override
    public void uploadCustomerBalance(List<VApar> listApar) throws Exception {
        if (!listApar.isEmpty()) {
            listApar.forEach(apar -> {
                String uniName = FontConventor.zg12uni51(apar.getTraderName());
                apar.setAccountCode(uniName);
                hmApar.put(apar.getTraderId(), apar);
            });
            Firestore firestore = FirestoreClient.getFirestore();
            ApiFuture<WriteResult> apiFuture = firestore.collection("cv-account").document("cus-balance").set(hmApar);
            WriteResult get = apiFuture.get();
            LOGGER.info("Uploaded Time :" + get.getUpdateTime());
        }

    }

}
