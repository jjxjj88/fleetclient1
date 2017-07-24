package com.headwin.fleet.pojo;

/**
 * Created by lnc on 2017/7/5.
 */

public class Dict {

    public Dict(){

    }

    public Dict(String key, String value){
        this.key = key;
        this.value = value;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String key;
    private String value;

    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Dict){
            Dict objDict = (Dict) obj;
            return key.equals(objDict.getKey());
        }
        return false;
    }
}
