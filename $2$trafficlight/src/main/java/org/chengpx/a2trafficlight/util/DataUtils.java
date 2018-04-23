package org.chengpx.a2trafficlight.util;

/**
 * create at 2018/4/23 17:28 by chengpx
 */
public class DataUtils {

    public static String obj2str(Object obj) {
        String str = null;
        if (obj instanceof Integer) {
            Integer i = (Integer) obj;
            str = i + "";
        } else if (obj instanceof Double) {
            Double d = (Double) obj;
            str = d + "";
        } else {
            str = (String) obj;
        }
        return str;
    }

}
