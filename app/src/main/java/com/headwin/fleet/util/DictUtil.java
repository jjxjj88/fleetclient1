package com.headwin.fleet.util;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.headwin.fleet.R;
import com.headwin.fleet.pojo.Dict;
import com.headwin.fleet.pojo.Task;
import com.headwin.fleet.pojo.Yard;
import com.headwin.fleet.pojo.resp.RespList;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.springframework.http.converter.json.GsonHttpMessageConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by lnc on 2017/7/6.
 */

public class DictUtil {

    public static List<Yard> yardList = new ArrayList<Yard>();

    public static void refreshYards(Context context){
        clearYards();
        String url = FleetUtil.getUrl(context, R.string.load_yards_uri);

        OkHttpUtils.get().url(url)
                .addHeader("Authorization", FleetUtil.getCredential(context))
                .addParams("nameHeader","").build().execute(new StringCallback(){
                @Override
                public void onError(Call call, Exception e, int id) {

                }

                @Override
                public void onResponse(String response, int id) {
                    GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
                    java.lang.reflect.Type type = new TypeToken<RespList<Yard>>() {}.getType();
                    RespList<Yard> respList = gsonHttpMessageConverter.getGson().fromJson(response, type);

                    if(respList.getData() != null){
                        yardList = respList.getData();
                    }
                }
            });
    }

    public static List<Yard>  getYards(){
        return yardList;
    }

    public static void clearYards(){
        yardList.clear();
    }

    public static List<Dict> getLastDay(){
        List<Dict> dayLists = new ArrayList<Dict>();
        Dict dictToday = new Dict("0", "今日");
        Dict dictTomorrow = new Dict("1", "次日");
        dayLists.add(dictToday);
        dayLists.add(dictTomorrow);
        return dayLists;
    }

    public static  List<Dict> getHours(){
        List<Dict> hourLists = new ArrayList<Dict>();
        for(int i=0; i < 24; i++){
            Dict dict = new Dict(i+ "", String.format("%s点", i));
            hourLists.add(dict);
        }
        return  hourLists;
    }
}
