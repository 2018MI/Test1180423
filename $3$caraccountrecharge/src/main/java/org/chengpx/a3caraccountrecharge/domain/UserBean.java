package org.chengpx.a3caraccountrecharge.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * create at 2018/4/30 17:21 by chengpx
 */
@DatabaseTable(tableName = "user")
public class UserBean {

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(columnName = "uname")
    private String uname;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "id=" + id +
                ", uname='" + uname + '\'' +
                '}';
    }

}
