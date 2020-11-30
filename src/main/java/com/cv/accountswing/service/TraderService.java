/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.entity.Trader;
import java.util.List;

/**
 *
 * @author WSwe
 */
public interface TraderService {

    public Trader findById(Integer id);

    public List<Trader> searchTrader(String code, String name, String address,
            String phone, String parentCode, String compCode, String appTraderCode);

    public Trader saveTrader(Trader trader);
}
