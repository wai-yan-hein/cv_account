/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.AccountDao;
import com.cv.accountswing.entity.AppUser;
import java.util.List;
import javax.naming.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author winswe
 */
@Service
@Transactional
public class AccountServiceImpl implements AccountService{
    
    @Autowired
    private AccountDao dao;
    
    @Override
    public AppUser saveAccount(AppUser au){
        dao.saveAccount(au);
        return au;
    }
    
    @Override
    public AppUser findUserById(Integer id){
        AppUser au = dao.findUserById(id);
        return au;
    }
    
    @Override
    public AppUser findUserByShort(String userShort){
        AppUser au = dao.findUserByShort(userShort);
        return au;
    }
    
    @Override
    public AppUser findUserByEmail(String email){
        AppUser au = dao.findUserByEmail(email);
        return au;
    }
    
    @Override
    public List<AppUser> search(String id, String userShort, String email, String owner){
        List<AppUser> listAU = dao.search(id, userShort, email, owner);
        return listAU;
    }
    
    @Override
    public AppUser login(String user, String password) throws AuthenticationException{
        AppUser au = dao.login(user, password);
        return au;
    }
}
