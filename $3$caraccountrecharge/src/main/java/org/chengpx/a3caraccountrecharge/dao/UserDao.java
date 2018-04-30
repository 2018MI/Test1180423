package org.chengpx.a3caraccountrecharge.dao;

import android.content.Context;

import org.chengpx.a3caraccountrecharge.domain.UserBean;
import org.chengpx.mylib.db.BaseDao;

/**
 * create at 2018/4/30 17:24 by chengpx
 */
public class UserDao extends BaseDao<UserBean> {

    private static UserDao sUserDao;

    private UserDao(Context context) {
        super(context);
    }

    public static UserDao getInstance(Context context) {
        if (sUserDao == null) {
            synchronized (UserDao.class) {
                if (sUserDao == null) {
                    sUserDao = new UserDao(context);
                }
            }
        }
        return sUserDao;
    }

}
