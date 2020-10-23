/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.AppUser;
import java.util.List;
import javax.naming.AuthenticationException;

/**
 *
 * @author WSwe
 */
public interface AccountDao {
    public AppUser saveAccount(AppUser au);
    public AppUser findUserById(Integer id);
    public AppUser findUserByShort(String userShort);
    public AppUser findUserByEmail(String email);
    public List<AppUser> search(String id, String userShort, String email, String owner);
    public AppUser login(String user, String password) throws AuthenticationException;
    public int delete(String userId);
    public AppUser finfById(String id);
}
