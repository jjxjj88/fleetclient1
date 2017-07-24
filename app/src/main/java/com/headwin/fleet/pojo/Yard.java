package com.headwin.fleet.pojo;

/**
 * Created by lnc on 2017/7/6.
 */

public class Yard {

    //堆场code
    String code;
    //堆场名
    String cname;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    //堆场联系人
    String shortCode;


    @Override
    public String toString() {
        return cname;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Yard){
            Yard objYard = (Yard) obj;
            return code.equals(objYard.getCode());
        }
        return false;
    }
}
