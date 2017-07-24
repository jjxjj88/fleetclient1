package com.headwin.fleet.callback;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.headwin.fleet.R;
import com.headwin.fleet.activity.LoginActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.cookie.CookieJarImpl;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.CookieJar;
import okhttp3.Response;

/**
 * Created by lnc on 2017/6/29.
 */

public abstract class FleetCallback  extends Callback<String> {
    Context context;

    public FleetCallback(Context context){
        this.context = context;
    }

    @Override
    public void onError(Call call, Exception e, int id) {
         Toast.makeText(context , R.string.mes_auth_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public String parseNetworkResponse(Response response, int id) throws IOException {
        return response.body().string();
    }


    @Override
    public boolean validateReponse(Response response, int id) {
        if(response.code() == 401){
            //Intent intent = new Intent(context, LoginActivity.class);
           // context.startActivity(intent);

            Toast.makeText(context , R.string.mes_auth_error, Toast.LENGTH_SHORT).show();

            CookieJar cookieJar = OkHttpUtils.getInstance().getOkHttpClient().cookieJar();
            ((CookieJarImpl) cookieJar).getCookieStore().removeAll();
            return false;
        }
        //Toast.makeText(context , R.string.mes_auth_error, Toast.LENGTH_SHORT).show();
        return super.validateReponse(response, id);
    }


}
