package org.chengpx.a3caraccountrecharge.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * create at 2018/4/24 9:36 by chengpx
 */
@DatabaseTable(tableName = "car")
public class CarBean {

    @DatabaseField(generatedId = true)
    private Integer id;
    /**
     * CarId : 2
     */
    @DatabaseField(columnName = "CarId")
    private Integer CarId;
    /**
     * Balance : 68
     */
    @DatabaseField(persisted = false)// 该字段不持久化
    private Integer Balance;
    /**
     * Money : 200
     */
    @DatabaseField(columnName = "Money")
    private Integer Money;
    /**
     * 充值时间
     */
    @DatabaseField(columnName = "rechargeDate")
    private Date rechargeDate;
    @DatabaseField(columnName = "fk_uid", foreignAutoRefresh = true, foreignColumnName = "id", foreign = true)
    private UserBean user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCarId() {
        return CarId;
    }

    public void setCarId(Integer carId) {
        CarId = carId;
    }

    public Integer getBalance() {
        return Balance;
    }

    public void setBalance(Integer balance) {
        Balance = balance;
    }

    public Integer getMoney() {
        return Money;
    }

    public void setMoney(Integer money) {
        Money = money;
    }

    public Date getRechargeDate() {
        return rechargeDate;
    }

    public void setRechargeDate(Date rechargeDate) {
        this.rechargeDate = rechargeDate;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "CarBean{" +
                "id=" + id +
                ", CarId=" + CarId +
                ", Balance=" + Balance +
                ", Money=" + Money +
                ", rechargeDate=" + rechargeDate +
                ", user=" + user +
                '}';
    }

}
