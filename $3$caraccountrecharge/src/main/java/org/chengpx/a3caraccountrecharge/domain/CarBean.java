package org.chengpx.a3caraccountrecharge.domain;

/**
 * create at 2018/4/24 9:36 by chengpx
 */
public class CarBean {


    /**
     * CarId : 2
     */
    private int CarId;
    /**
     * Balance : 68
     */
    private int Balance;
    /**
     * Money : 200
     */
    private int Money;


    public int getCarId() {
        return CarId;
    }

    public void setCarId(int CarId) {
        this.CarId = CarId;
    }

    public int getBalance() {
        return Balance;
    }

    public void setBalance(int Balance) {
        this.Balance = Balance;
    }

    public int getMoney() {
        return Money;
    }

    public void setMoney(int Money) {
        this.Money = Money;
    }

    @Override
    public String toString() {
        return "CarBean{" +
                "CarId=" + CarId +
                ", Balance=" + Balance +
                ", Money=" + Money +
                '}';
    }

}
