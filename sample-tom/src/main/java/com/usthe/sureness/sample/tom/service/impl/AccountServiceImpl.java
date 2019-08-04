package com.usthe.sureness.sample.tom.service.impl;

import com.usthe.sureness.sample.tom.dao.AuthUserDao;
import com.usthe.sureness.sample.tom.pojo.dto.Account;
import com.usthe.sureness.sample.tom.pojo.entity.AuthUserDO;
import com.usthe.sureness.sample.tom.service.AccountService;
import com.usthe.sureness.util.Md5Util;
import com.usthe.sureness.util.SurenessCommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author tomsun28
 * @date 10:58 2019-08-04
 */
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AuthUserDao authUserDao;

    @Override
    public boolean authenticateAccount(Account account) {
        Optional<AuthUserDO> authUserOptional = authUserDao.findAuthUserByUsername(account.getUsername());
        if (!authUserOptional.isPresent()) {
            return false;
        }
        AuthUserDO authUser = authUserOptional.get();
        String password = account.getPassword();
        if (!Objects.isNull(authUser.getSalt())) {
            // 用盐加密
            password = Md5Util.md5(password + authUser.getSalt());

        }
        return authUser.getPassword().equals(password);
    }

    @Override
    public List<String> loadAccountRoles(String username) {
        return authUserDao.findAccountOwnRoles(username);
    }

    @Override
    public boolean registerAccount(Account account) {
        String salt = SurenessCommonUtil.getRandomString(6);
        String password = Md5Util.md5(account.getPassword() + salt);
        AuthUserDO authUser = AuthUserDO.builder().username(account.getUsername())
                .password(password).salt(salt).status(1).build();
        return authUserDao.save(authUser) != null;
    }

    @Override
    public boolean isAccountExist(Account account) {
        Optional<AuthUserDO> authUserOptional = authUserDao.findAuthUserByUsername(account.getUsername());
        return authUserOptional.isPresent();
    }
}
