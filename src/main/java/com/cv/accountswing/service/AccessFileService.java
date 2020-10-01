/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

/**
 *
 * @author winswe
 */
public interface AccessFileService {
    public void uploadGL(String path, int compId);
    public void uploadTrader(String path, int compId);
    public void uploadOpening(String path, int compId);
}
