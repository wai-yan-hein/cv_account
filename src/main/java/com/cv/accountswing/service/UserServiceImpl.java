/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.AccountDao;
import com.cv.accountswing.entity.AppUser;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author winswe
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private AccountDao dao;

    @Override
    public AppUser save(AppUser user) {
        dao.saveAccount(user);
        return user;
    }

    @Override
    public List<AppUser> search(String id, String userShort, String email, String owner) {
        return dao.search(id, userShort, email, owner);
    }

    @Override
    public int delete(String userId) {
        return dao.delete(userId);
    }

    @Override
    public AppUser login(String userShort, String password) {
        List<AppUser> listAU = dao.search("-", userShort, "-", "-");
        AppUser au = null;

        if (listAU != null) {
            if (!listAU.isEmpty()) {
                au = listAU.get(0);
            }
        }

        return au;
    }

    @Override
    public AppUser findById(String id) {
        return dao.finfById(id);
    }
}
