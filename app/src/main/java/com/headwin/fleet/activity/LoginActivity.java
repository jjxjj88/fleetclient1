package com.headwin.fleet.activity;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.headwin.fleet.R;
import com.headwin.fleet.callback.FleetCallback;
import com.headwin.fleet.pojo.Dict;
import com.headwin.fleet.pojo.User;
import com.headwin.fleet.util.DictUtil;
import com.headwin.fleet.util.FleetUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.cookie.CookieJarImpl;

import java.nio.charset.Charset;

import okhttp3.Call;
import okhttp3.CookieJar;
import okhttp3.Credentials;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_login);
            hideActionBar();
            Button btnLogin = (Button) findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    EditText edtUsername = (EditText) findViewById(R.id.username);
                    EditText edtPassword = (EditText) findViewById(R.id.password);

                    final String username = edtUsername.getText().toString();
                    final String password = edtPassword.getText().toString();

                    String url = FleetUtil.getUrl(getApplication(), R.string.auth_uri);
                    final String credential = Credentials.basic(username, password, Charset.forName("UTF-8"));

                    DisplayMetrics metrics = FleetUtil.getMetrics(getApplicationContext());

                    OkHttpUtils.get().url(url)
                            .addHeader("Authorization",credential)
                            .addParams("appCode",FleetUtil.getDeviceMac(getApplicationContext()))
                            .addParams("systemType", "Android " + Build.VERSION.RELEASE)
                            .addParams("systemVersion",Build.VERSION.SDK_INT+"")
                            .addParams("versionNum", FleetUtil.getVerCode(getApplicationContext())+"")
                            .addParams("deceiveWidth",FleetUtil.getWidthPix(metrics)+"")
                            .addParams("deceiveHigh",FleetUtil.getHeightPix(metrics)+"")
                            .build().execute(new FleetCallback(LoginActivity.this) {

                        @Override
                        public void onResponse(String response, int id) {
                            //Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
                            User user = new Gson().fromJson(response, User.class);
                            FleetUtil.persistentUser(getApplication(), user, credential);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            LoginActivity.this.finish();
                        }
                    });
                }
            });
    }

    /**
     * 隐藏标题栏
     */
    void hideActionBar(){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.hide();
    }
}
