/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.entity.AppUser;
import java.util.List;

/**
 *
 * @author winswe
 */
public interface UserService {
    public AppUser save(AppUser user);
    public List<AppUser> search(String id, String userShort, String email, String owner);
    public int delete(String userId);
    public AppUser login(String userShort, String password);
    public AppUser findById(String id);
}
