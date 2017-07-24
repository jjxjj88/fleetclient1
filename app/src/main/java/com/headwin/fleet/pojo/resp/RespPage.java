package com.headwin.fleet.pojo.resp;

/**
 * Created by lnc on 2017/6/30.
 */

public class RespPage<T> extends  BaseResp{

    private PageImpl<T> data;

    public RespPage() {
    }

    public PageImpl<T> getData() {
        return data;
    }

    public void setData(PageImpl<T> data) {
        this.data = data;
    }

}
