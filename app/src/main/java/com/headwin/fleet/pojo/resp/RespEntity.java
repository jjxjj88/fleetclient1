package com.headwin.fleet.pojo.resp;

/**
 * Created by lnc on 2017/7/5.
 */

public class RespEntity<T> extends BaseResp {

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
