/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.service;

import com.cv.inv.entity.MachineInfo;

/**
 *
 * @author lenovo
 */
public interface MachineInfoService {

    public MachineInfo save(MachineInfo machineInfo) throws Exception;

    public int getMax(String machineName) throws Exception;

}
