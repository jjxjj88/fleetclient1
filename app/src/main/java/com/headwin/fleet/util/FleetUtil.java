package com.headwin.fleet.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.StringRes;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.EventLogTags;
import android.util.Log;

import com.headwin.fleet.R;
import com.headwin.fleet.pojo.Dict;
import com.headwin.fleet.pojo.User;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.Response;

/**
 * Created by lnc on 2017/6/21.
 */

public class FleetUtil {

    private static final String TAG = "FleetUtil";

    private static final String LOGIN_NAME = "loginName";
    private static final String ACCOUNT_CODE = "accountCode";
    private static final String CREDENTIAL = "credential";
    private static final String MOBILE_NO = "mobileNo";
    private static final String FLEET_NAME = "fleetName";


    /**
     * 取机器媒体信息
     * @param context
     * @return
     */
    public static DisplayMetrics getMetrics(Context context){
        DisplayMetrics dm=context.getResources().getDisplayMetrics();
        return  dm;
    }

    /**
     * 取屏幕宽度
     * @param dm
     * @return
     */
    public static int getWidthPix(DisplayMetrics dm){
        return dm.widthPixels;
    }

    public static String getDeviceMac(Context context){
        String mac = "";
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null
                && networkInfo.isAvailable()
                && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            WifiManager wm = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            mac = wm.getConnectionInfo().getMacAddress();
        } else {
            mac = UUID.randomUUID().toString();
        }
        return mac;
    }

    /**
     * 取设备号
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        String deviceId = null;
        deviceId = ((TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if (deviceId == null && Build.VERSION.SDK_INT > 9) {
            deviceId = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            if (deviceId == null) {
                deviceId = getDeviceMac(context);
            }
        }

        if (deviceId != null && deviceId.length() < 28) {
            int len = 28 - deviceId.length();
            for (int i = 0; i < len; i++) {
                deviceId = "0" + deviceId;
            }
        }

        return deviceId;
    }

    /**
     * 取屏幕高度
     * @param dm
     * @return
     */
    public static int getHeightPix(DisplayMetrics dm){
        return dm.heightPixels;
    }

    //public static

    public static String getUrl(Context context, @StringRes int resId){
        //Application.getgetApplicationContext
        return context.getString(R.string.base_uri) + context.getString(resId);
    }
    /**
     * 保存用户信息
     */
    public static void persistentUser(Context content, User user, String credential){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(content);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LOGIN_NAME,user.getLoginName());
        editor.putString(ACCOUNT_CODE, user.getAccountCode());
        editor.putString(MOBILE_NO, user.getMobileNo());
        editor.putString(FLEET_NAME, user.getN7Code());
        editor.putString(CREDENTIAL, credential);

        editor.apply();
    }

    /**
     *  清除登录信息
     * @param content
     */
    public static void clearPersistentUser(Context content){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(content);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /*
    * 取得当前用户登录名
    * */
    public static String getLoginName(Context content){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(content);
        String loginName = sharedPreferences.getString(LOGIN_NAME, "");
        return loginName;
    }

    /*
    * 取得当前用户账号
    * */
    public static String getAccountCode(Context content){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(content);
        String accountCode = sharedPreferences.getString(ACCOUNT_CODE, "");
        return accountCode;
    }

    /**
     * 取车队信息
     * @param content
     * @return
     */
    public static String getFleetName(Context content){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(content);
        String fleetName = sharedPreferences.getString(FLEET_NAME, "");
        return fleetName;
    }

    /**
     * 取登录人手机号
     * @param content
     * @return
     */
    public static String getMobileNo(Context content){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(content);
        String mobileNo = sharedPreferences.getString(MOBILE_NO, "");
        return mobileNo;
    }

    /*
    * 取得当前base64凭证
    * */
    public static String getCredential(Context content){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(content);
        String credential = sharedPreferences.getString(CREDENTIAL, "");
        return credential;
    }



    public static String getCookie(Context context){
        CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(context));
        List<Cookie> cookies = cookieJar.getCookieStore().getCookies();
        return cookies.get(0).toString();
    }

    public static int getVerCode(Context context) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo(
                    "com.headwin.fleet", 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return verCode;
    }
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    "com.headwin.fleet", 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return verName;
    }

    public static String getTareVerifyMessage(String tare){
        if (tare == null || tare.trim().length() <= 0 )
            return "皮重不能为空！";
        return null;
    }

    public static String getYardVerifyMessae(String yardCode){
        if (yardCode == null || yardCode.trim().length() <= 0 )
            return "场请选择堆！";
        return null;
    }

    public static String getSealVerifyMessage(String sealNo){
        if (sealNo == null || sealNo.trim().length() <= 0 )
            return "封号不能为空！";
        return null;
    }

    public static String getCtnrVerifyMessage(String ctnr) {
        if (ctnr == null || ctnr.trim().length() != 11)
            return "箱号必须是 11 位！";
        ctnr = ctnr.toUpperCase();
        // 前四位为字符，且第四位＝‘U’

        char c3 = ctnr.charAt(3);
        // if ( (c3!= 'U' && c3!= 'S') || ctnr.charAt(0) < 'A' || ctnr.charAt(0)
        // > 'Z' || ctnr.charAt(1) < 'A' || ctnr.charAt(1) > 'Z' ||
        // ctnr.charAt(2) < 'A' || ctnr.charAt(2) > 'Z') // 第四位为U
        // return "箱号前 4 位必须为字母，第 4 位字母必须是 U！";
        if (c3 < 'A' || c3 > 'Z' || ctnr.charAt(0) < 'A' || ctnr.charAt(0) > 'Z' || ctnr.charAt(1) < 'A' || ctnr.charAt(1) > 'Z' || ctnr.charAt(2) < 'A' || ctnr.charAt(2) > 'Z')
            return "箱号前 4 位必须为字母！";
        int srcVerifyCode = ctnr.charAt(10) - '0';
        // 检查箱号是否合法

        int verifyCode = getCtnrVerifyCode0(ctnr.substring(0, 10));
        if (srcVerifyCode != verifyCode)
            return "箱号第11位校验位错误，正确校验位为：" + verifyCode + "！";
        return null;
    }

    public static int getCtnrVerifyCode0(String ctnr)
    {
        if (ctnr == null)
            return -1;
        int len = ctnr.length();
        if (len != 10 && len != 11)
            return -1;
        ctnr = ctnr.toUpperCase();
        int sum = 0;
        for (int i = 0; i < 10; i++)
        {
            int baseValue = 0;
            char c = ctnr.charAt(i);
            if (c >= '0' && c <= '9')
                baseValue = (int) c - '0';
            else if (c == 'A')
                baseValue = 10;
            else if (c >= 'B' && c <= 'K')
                baseValue = 12 + (c - 'B');
            else if (c >= 'L' && c <= 'U')
                baseValue = 23 + (c - 'L');
            else if (c >= 'V' && c <= 'Z')
                baseValue = 34 + (c - 'V');
            else
                return -1;
            sum += baseValue * (1 << i);
        }
        int j = sum % 11;
        return j > 9 ? 0 : j;
    }

}
