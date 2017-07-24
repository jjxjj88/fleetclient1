package com.headwin.fleet.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.headwin.fleet.R;
import com.headwin.fleet.pojo.Version;
import com.headwin.fleet.pojo.resp.RespEntity;
import com.headwin.fleet.service.UpdataService;
import com.headwin.fleet.util.FleetUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.cookie.CookieJarImpl;

import org.springframework.http.converter.json.GsonHttpMessageConverter;

import java.net.UnknownHostException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.CookieJar;

public class LoadingActivity extends AppCompatActivity {

    private static int REQUESTPERMISSION = 110 ;

    LinearLayout layoutLoading;

    //后台更新版本服务
    private Intent updateService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        //Intent updateIntent = new Intent(this, UpdataService.class);
        //startService(updateIntent);


        hideLoading();


    }

    /*
    * */
    void hideLoading(){
        AlphaAnimation animation=new AlphaAnimation(1.0f,1.0f);
        animation.setDuration(1000);
        layoutLoading= (LinearLayout) findViewById(R.id.layoutLoading);
        layoutLoading.setAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                layoutLoading.setSystemUiVisibility(View.INVISIBLE);
                layoutLoading.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                String url = FleetUtil.getUrl(getApplicationContext(), R.string.check_version_uri);
                final int vercode = FleetUtil.getVerCode(getApplicationContext());
                //String verName = FleetUtil.getVerName(this);
                OkHttpUtils.get().url(url).addParams("versionNum",vercode+"").build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if(e instanceof UnknownHostException){
                            Toast.makeText(getApplicationContext(), R.string.mes_timeout_error, Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getApplicationContext(), R.string.mes_load_error, Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
                        java.lang.reflect.Type type = new TypeToken<RespEntity<Version>>() {}.getType();
                        RespEntity<Version> respEntity = gsonHttpMessageConverter.getGson().fromJson(response, type);
                        //最新版本
                        if(respEntity.getData() == null){
                            normal();
                        }else{
                            final Version version =  respEntity.getData();
                            haveNewVersion(version);
                        }
                    }
                });

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUESTPERMISSION){
            if(permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(updateService !=null)
                        startService(updateService);
                }else{
//提示没有权限，安装不了咯
                }
            }
        }
    }


    private void haveNewVersion(final  Version version){
        //final Version version =  respEntity.getData();
        Dialog dialog = new AlertDialog.Builder(LoadingActivity.this)
                .setTitle(R.string.app_soft_update)
                .setMessage(version.getDescription())
                .setPositiveButton(R.string.app_update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent updateService = new Intent(LoadingActivity.this, UpdataService.class);
                        //startService(updateIntent);
                        String getApkUrl =  FleetUtil.getUrl(getApplicationContext(), R.string.get_apk_uri) + "?versionCode="+ version.getVersionCode();
                        updateService.putExtra("url",getApkUrl);
                        if(ContextCompat.checkSelfPermission(LoadingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//申请权限
                            ActivityCompat.requestPermissions(LoadingActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTPERMISSION);
                            //Toast.showToast("请允许权限进行下载安装");
                        }else{
                            startService(updateService);
                        }
                    }
                })
                .setNegativeButton(R.string.app_not_update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        normal();
                    }
                })
                .create();
        dialog.setCanceledOnTouchOutside(false);
        if(!isFinishing()){
            dialog.show();
        }
        //dialog.show();
    }

    private void normal(){

        layoutLoading.setVisibility(View.GONE);


        //检查cookie
        //CookieJar cookieJar = OkHttpUtils.getInstance().getOkHttpClient().cookieJar();
        //List<Cookie> cookieList = ((CookieJarImpl) cookieJar).getCookieStore().getCookies();
        Intent intent;
        if(!"".equals(FleetUtil.getCredential(getApplicationContext()))){
            intent = new Intent(LoadingActivity.this, MainActivity.class);
            //((CookieJarImpl) cookieJar).getCookieStore().removeAll();
        }else{
            intent = new Intent(LoadingActivity.this, LoginActivity.class);
        }
        startActivity(intent);
        LoadingActivity.this.finish();
    }

}
