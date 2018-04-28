package org.chengpx.a3caraccountrecharge.dao;

import android.content.Context;

import org.chengpx.a3caraccountrecharge.domain.CarBean;
import org.chengpx.mylib.db.BaseDao;

/**
 * create at 2018/4/28 8:42 by chengpx
 */
public class CarDao extends BaseDao<CarBean> {

    private static CarDao sCarDao;

    private CarDao(Context context) {
        super(context);
    }

    public static CarDao getInstance(Context context) {
        if (sCarDao == null) {
            synchronized (CarDao.class) {
                if (sCarDao == null) {
                    sCarDao = new CarDao(context);
                }
            }
        }
        return sCarDao;
    }
}
