package org.chengpx.a3caraccountrecharge.domain;

/**
 * create at 2018/4/24 12:35 by chengpx
 */
public class ServerInfoBean {

    /**
     * result : ok
     */
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ServerInfoBean{" +
                "result='" + result + '\'' +
                '}';
    }

}
