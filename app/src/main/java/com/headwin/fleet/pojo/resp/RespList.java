package com.headwin.fleet.pojo.resp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lnc on 2017/6/30.
 */

public class RespList<T> extends  BaseResp {


    private List<T> data = new ArrayList<T>();

    public RespList() {
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
