package org.chengpx.a3caraccountrecharge;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.chengpx.a3caraccountrecharge.dao.UserDao;
import org.chengpx.a3caraccountrecharge.domain.UserBean;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("org.chengpx.a3caraccountrecharge", appContext.getPackageName());
    }

    @Test
    public void test1() {
        UserBean userBean = new UserBean();
        userBean.setUname("admin");
        int insert = UserDao.getInstance(InstrumentationRegistry.getTargetContext()).insert(userBean);
        System.out.println("UserDao insert: " + insert);
    }

}
