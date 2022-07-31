package com.abc.p2p.vo;

import java.io.Serializable;

public class InvestTopVo implements Serializable {
    public String phone;
    public Double bidmMoney;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getBidmMoney() {
        return bidmMoney;
    }

    public void setBidmMoney(Double bidmMoney) {
        this.bidmMoney = bidmMoney;
    }
}
