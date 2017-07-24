package com.headwin.fleet.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.headwin.fleet.R;
import com.headwin.fleet.pojo.Task;
import com.headwin.fleet.pojo.UpdateStatus;
import com.headwin.fleet.pojo.resp.RespEntity;
import com.headwin.fleet.util.FleetUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import org.springframework.http.converter.json.GsonHttpMessageConverter;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;

public class DetailActivity extends AppCompatActivity {

    private static final String LOGISTICAL_CODE = "logisticalCode";

    private static final String STATUS_SUITCASE = "2";
    private static final String STATUS_INTO_FACTORY  = "4";
    private static final String STATUS_LEAVE_FACTORY = "8";
    private static final String STATUS_INTO_PORT = "16";

    private static final String RESPONSE_SUCCESS = "0000";
    private static final String RESPONSE_ERROR = "1111";

    LinearLayout layoutRoot;

    CircleImageView imgSuitcase, imgIntoFactory, imgLeaveFactory, imgIntoPort;
    TextView labSuitcase, labIntoFactory, labLeaveFactory, labIntoPort, labEtime, labLoadTime;
    TextView labFactoryName,labFactoryAddress, labFactoryContact, labFactoryMobileno, labFactoryPhone, labRemark;
    TextView labContainerType, labContainerNo, labSealNo, labTare, labYardName, labDockName;

    String logisticalCode;
    Task task;

    public static void  actionStart(Context context, Task task){
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(LOGISTICAL_CODE, task.getLogisticalCode());
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        logisticalCode = intent.getStringExtra(LOGISTICAL_CODE);

        initView();
        addListener();
        loadTask(logisticalCode);

    }

    /**
     * 工具栏相关设置
     * @param task
     */
    private void setActionBar(Task task) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.a);
        actionBar.setTitle(task.getBookingNo());
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            //时间返回
            case 0:
                if(resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();
                    final String dayKey = bundle.getString("dayKey");
                    final String hoursKey = bundle.getString("hoursKey");

                    final String containerNo = bundle.getString("containerNo");
                    final String sealNo = bundle.getString("sealNo");
                    final String tare = bundle.getString("tare");
                    final String yardCode = bundle.getString("yardCode");

                    String url = FleetUtil.getUrl(getApplicationContext(), R.string.login_uri);
                    OkHttpUtils.head().url(url)
                            .addHeader("Authorization", FleetUtil.getCredential(getApplicationContext()))
                            .build().execute(new Callback(){
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Toast.makeText(getApplicationContext(), "获取服务器失败", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(Object response, int id) {
                            submitContainInfo(dayKey, hoursKey, containerNo, sealNo, tare, yardCode, response);
                        }

                        @Override
                        public Object parseNetworkResponse(Response response, int id) throws Exception {
                            long serverIntDate  = response.receivedResponseAtMillis();
                            Date serverDate = new Date(serverIntDate);
                            return serverDate;
                        }
                    });
                }
            default:
        }


    }

    /**
     * 重新从服务器加载Task类
     * @param logisticalCode
     */
    private void loadTask(String logisticalCode) {
        String url = FleetUtil.getUrl(this, R.string.load_task_uri);
        String accountCode = FleetUtil.getAccountCode(this);
        OkHttpUtils.get().url(url)
                .addHeader("Authorization", FleetUtil.getCredential(getApplicationContext()))
                .addParams("logisticalCode", logisticalCode).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if(e instanceof UnknownHostException){
                    Toast.makeText(getApplicationContext(), R.string.mes_timeout_error, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), R.string.mes_load_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onResponse(String response, int id) {
                GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
                java.lang.reflect.Type type = new TypeToken<RespEntity<Task>>() {}.getType();
                RespEntity<Task> respEntity = gsonHttpMessageConverter.getGson().fromJson(response, type);
                if(respEntity.getData() != null){
                    task = respEntity.getData();
                    initData(task);
                    setActionBar(task);
                }
            }
        });

    }

    /**
     * 初始化控件
     */
    private void initView(){

        layoutRoot = (LinearLayout)findViewById(R.id.layout_root);

        imgSuitcase = (CircleImageView) findViewById(R.id.img_suitcase);
        imgIntoFactory = (CircleImageView) findViewById(R.id.img_into_factory);
        imgLeaveFactory = (CircleImageView) findViewById(R.id.img_leave_factory);
        imgIntoPort = (CircleImageView) findViewById(R.id.img_into_port);

        labSuitcase = (TextView) findViewById(R.id.lab_suitcase);
        labIntoFactory = (TextView) findViewById(R.id.lab_into_factory);
        labLeaveFactory = (TextView) findViewById(R.id.lab_leave_factory);
        labIntoPort = (TextView) findViewById(R.id.lab_into_port);
        labEtime = (TextView) findViewById(R.id.lab_etime);
        labLoadTime = (TextView) findViewById(R.id.lab_load_time);


        labContainerType = (TextView) findViewById(R.id.lab_container_type);
        labContainerNo = (TextView) findViewById(R.id.lab_container_no);
        labSealNo = (TextView) findViewById(R.id.lab_seal_no);
        labTare  = (TextView) findViewById(R.id.lab_tare);
        labYardName  = (TextView) findViewById(R.id.lab_yard_name);
        labDockName = (TextView) findViewById(R.id.lab_dock_name);

        labFactoryName = (TextView) findViewById(R.id.lab_factory_name);
        labFactoryAddress = (TextView) findViewById(R.id.lab_factory_address);
        labFactoryContact = (TextView) findViewById(R.id.lab_factory_contact);
        labFactoryMobileno = (TextView) findViewById(R.id.lab_factory_mobileno);
        labFactoryPhone = (TextView) findViewById(R.id.lab_factory_phone);
        labRemark = (TextView) findViewById(R.id.lab_remark);

    }

    /**
     * 初始化数据
     * @param task
     */
    private void initData(Task task) {
        reViewStatus(task);
        setEtime(task);
        setFactoryInfo(task);
        setContainerInfo(task);
        setDockInfo(task);
    }

    private void setDockInfo(Task task) {
        labDockName.setText(task.getWharfName());
    }

    private void setContainerInfo(Task task) {
        labContainerType.setText(task.getContainerInfo());
        labContainerNo.setText(task.getContainerNo());
        labSealNo.setText(task.getSealNo());
        labTare.setText(String.format("%5.2f KG",task.getTare()));
        labYardName.setText(task.getYardName());

    }

    /**
     * 图片重新布局
     * @param task
     */
    private void reViewStatus(Task task){
        int logisticalStatus  = task.getLogisticalStatus();
        setImgAndLab(imgSuitcase, labSuitcase, isHappend(logisticalStatus, STATUS_SUITCASE), "lab_suitcase");
        setImgAndLab(imgIntoFactory, labIntoFactory, isHappend(logisticalStatus, STATUS_INTO_FACTORY), "lab_into_factory");
        setImgAndLab(imgLeaveFactory, labLeaveFactory, isHappend(logisticalStatus, STATUS_LEAVE_FACTORY), "lab_leave_factory");
        setImgAndLab(imgIntoPort, labIntoPort, isHappend(logisticalStatus, STATUS_INTO_PORT), "lab_into_port");
    }


    /**
     * 显示工厂相关信息
     * @param task
     */
    private void setFactoryInfo(Task task) {
        labLoadTime.setText(task.getFirstTime());
        labFactoryName.setText(task.getFactoryName());
        labFactoryAddress.setText(task.getFactoryAddress());
        labFactoryContact.setText(task.getFactoryContact());
        labFactoryMobileno.setText(task.getFactoryMobileNo());
        labFactoryPhone.setText(task.getFactoryPhone());
        labRemark.setText(task.getRemark());
    }

    /**
     *  显示预计出厂时间
     */
    private void setEtime(Task task) {
        String expectFactoryTime =  task.getExpectFactoryTime();
        if(expectFactoryTime == null || "".equals(expectFactoryTime)){
            labEtime.setText("");
        }else{
            long timeLong = Long.parseLong(expectFactoryTime);
            Date expectFactory = new Date(timeLong);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String eTimeStr = sdf.format(expectFactory);
            labEtime.setText(eTimeStr);
        }
    }


    /**
     * 根据状态值展现图片状态
     * @param circleImageView
     * @param textView
     * @param isHappend
     * @param lab
     */
    private void setImgAndLab(CircleImageView circleImageView,  TextView textView, boolean isHappend, String lab){
        if(isHappend){
            circleImageView.setColorFilter(null);
            textView.setTextColor(this.getResources().getColor(R.color.colorPrimaryDark));
            circleImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Dialog dialog = new AlertDialog.Builder(DetailActivity.this)
                            .setTitle(R.string.lab_prompt)
                            .setMessage(R.string.mes_reback)
                            .setPositiveButton(R.string.lab_confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    reBack();
                                }
                            })
                            .setNegativeButton(R.string.lab_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create();
                    dialog.show();
                    return false;
                }
            });
        }else{
            textView.setTextColor(this.getResources().getColor(R.color.colorGray));
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0); // 设置饱和度
            ColorMatrixColorFilter grayColorFilter = new ColorMatrixColorFilter(cm);
            circleImageView.setColorFilter(grayColorFilter);
        }
        textView.setText(getResIdForLab(isHappend, lab));
    }


    private int getResIdForLab(boolean isHappend, String lab){
        return getApplicationContext().getResources().getIdentifier(lab+(isHappend ? "_ed":""), "string", getApplication().getPackageName());
    }

    /**
     *  判断某个状态位是否为1
     */
    private boolean isHappend(int logisticalStatus, String bitStr){
        int recBit = Integer.parseInt(bitStr);
        return ((logisticalStatus & recBit) == recBit);
    }

    /**
     * 对界面上的控件增加监听
     */
    private void addListener(){
        imgSuitcase.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(!isHappend(task.getLogisticalStatus(), STATUS_SUITCASE)){

                    String dayKey = getDayForTime(task.getExpectFactoryTime());
                    String hoursKey = getHoursForTime(task.getExpectFactoryTime());
                    String containerNo = task.getContainerNo();
                    String sealNo = task.getSealNo();
                    String tare = String.format("%5.2f", task.getTare());
                    String yardCode = task.getYardCode();

                    Intent intent = new Intent(DetailActivity.this, ContainerActivity.class);
                    intent.putExtra("dayKey", dayKey);
                    intent.putExtra("hoursKey", hoursKey);
                    intent.putExtra("containerNo", containerNo);
                    intent.putExtra("sealNo", sealNo);
                    intent.putExtra("tare", tare);
                    intent.putExtra("yardCode", yardCode);
                    startActivityForResult(intent, 0);
                }
            }
        });
        imgIntoFactory.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                uploadStatus(STATUS_INTO_FACTORY, "进厂失败，进厂前必须先提箱！");
            }
        });
        imgLeaveFactory.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                uploadStatus(STATUS_LEAVE_FACTORY, "出厂失败，出厂前必须先进厂！");
            }
        });
        imgIntoPort.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                uploadStatus(STATUS_INTO_PORT,  "进港失败，进港前必须先出厂！");
            }
        });


        labFactoryMobileno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dial(v);
            }
        });
        labFactoryPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dial(v);
            }
        });

    }

    private String getHoursForTime(String expectFactoryTime) {
        if(expectFactoryTime == null || "".equals(expectFactoryTime)){
            return "0";
        }else{
            long timeLong = Long.parseLong(expectFactoryTime);
            Date expectFactory = new Date(timeLong);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(expectFactory);
            return  String.format("%d", calendar.get(Calendar.HOUR_OF_DAY));
        }
    }


    private String getDayForTime(String expectFactoryTime) {
        if(expectFactoryTime == null || "".equals(expectFactoryTime)){
            return "0";
        }else{
            long timeLong = Long.parseLong(expectFactoryTime);
            Date expectFactory = new Date(timeLong);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(expectFactory);

            Date nowDate = new Date();
            Calendar nowCal = Calendar.getInstance();
            nowCal.setTime(nowDate);

            return calendar.get(Calendar.DAY_OF_YEAR) ==  calendar.get(Calendar.DAY_OF_YEAR) ? "0" : "1";

        }
    }


    private  boolean checkStatus(String currentStatus){
        int intStatus = Integer.parseInt(currentStatus);
        if(intStatus <= 2){
            return true;
        }else{
            int preStatus = intStatus/2;
            int logisticalStatus = task.getLogisticalStatus();
            return ((logisticalStatus & preStatus) == preStatus);
        }
    }

    private void dial(View v){
        TextView textView = (TextView) v;
        String phoneNo = textView.getText().toString();
        if( phoneNo!= null && !"".equals(phoneNo)){
            phoneNo = phoneNo.trim();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(String.format("tel:%s", phoneNo)));
            startActivity(intent);
        }

    }

    /**
     * 计算时间并更新箱子相关信息
     * @param dayKey
     * @param hoursKey
     * @param containerNo
     * @param sealNo
     * @param tare
     * @param yardCode
     * @param response
     */
    private void submitContainInfo(String dayKey, String hoursKey, String containerNo, String sealNo, String tare, String yardCode, Object response) {
        Date serverDate = (Date) response;
        int dayInt = Integer.parseInt(dayKey);
        int HoursInt = Integer.parseInt(hoursKey);
        Calendar day = Calendar.getInstance();
        day.setTime(serverDate);
        day.add(Calendar.DATE, dayInt);
        day.set(Calendar.HOUR_OF_DAY, HoursInt);
        day.set(Calendar.MINUTE, 0);
        day.set(Calendar.SECOND, 0);
        Date eTime = day.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String eTimeStr = sdf.format(eTime);
        task.setExpectFactoryTime(day.getTimeInMillis()+"");
        updateContainerInfo(eTimeStr, containerNo, sealNo, tare, yardCode);
    }


    /**
     * 更新状态结点
     * @param updateStatus
     */
    private void uploadStatus(final String updateStatus, String errMes){
        //如果已经点上，防止重复提交
        if(isHappend(task.getLogisticalStatus(), updateStatus)){
            return;
        }
        if (checkStatus(updateStatus)) {
            String url = FleetUtil.getUrl(getApplication(), R.string.update_logistical_status_uri);
            OkHttpUtils.post().url(url)
                    .addParams("logisticalCode", logisticalCode)
                    .addParams("logisticalStatus", updateStatus)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            //Toast.makeText(getApplicationContext(), "ddd", Toast.LENGTH_LONG).show();
                            if(e instanceof UnknownHostException){
                                Toast.makeText(getApplicationContext(), R.string.mes_timeout_error, Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(), R.string.mes_option_error, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onResponse(String response, int id) {

                            GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
                            java.lang.reflect.Type type = new TypeToken<RespEntity<UpdateStatus>>() {}.getType();
                            RespEntity<UpdateStatus> respEntity = gsonHttpMessageConverter.getGson().fromJson(response, type);
                            if(RESPONSE_SUCCESS.equals(respEntity.getCode())){
                                loadTask(logisticalCode);
                            }
                        }
                    });
        }else{
            showMes(errMes);
        }

    }

    /**
     * 更新预计进厂时间
     * @param expectFactoryTime
     */
    public void updateInfoFactoryTime(final String expectFactoryTime){
        String url = FleetUtil.getUrl(getApplication(), R.string.update_into_factory_uri);
        OkHttpUtils.post().url(url)
                .addHeader("Authorization", FleetUtil.getCredential(getApplicationContext()))
                .addParams("logisticalCode", logisticalCode)
                .addParams("expectFactoryTime", expectFactoryTime)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(getApplicationContext(), "ddd", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
                        java.lang.reflect.Type type = new TypeToken<RespEntity<UpdateStatus>>() {}.getType();
                        RespEntity<UpdateStatus> respEntity = gsonHttpMessageConverter.getGson().fromJson(response, type);
                        if(RESPONSE_SUCCESS.equals(respEntity.getCode())){
                            //UpdateStatus updateStatus = respEntity.getData();
                            uploadStatus(STATUS_SUITCASE, "");
                        }
                    }
                });
    }


    /**
     * 更新箱子信息
     * @param containerNo
     * @param sealNo
     * @param tare
     * @param yardCode
     */
    public void updateContainerInfo(String  expectFactoryTime, String containerNo, String sealNo, String tare, String yardCode){
        String url = FleetUtil.getUrl(getApplication(), R.string.update_container_info_uri);
        OkHttpUtils.post().url(url)
                .addHeader("Authorization", FleetUtil.getCredential(getApplicationContext()))
                .addParams("logisticalCode", logisticalCode)
                .addParams("expectFactoryTime", expectFactoryTime)
                .addParams("containerNo", containerNo)
                .addParams("sealNo", sealNo)
                .addParams("tare", tare)
                .addParams("yardcode", yardCode)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        //Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG).show();
                        if(e instanceof UnknownHostException){
                            Toast.makeText(getApplicationContext(), R.string.mes_timeout_error, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(), R.string.mes_save_error, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
                        java.lang.reflect.Type type = new TypeToken<RespEntity<UpdateStatus>>() {}.getType();
                        RespEntity<UpdateStatus> respEntity = gsonHttpMessageConverter.getGson().fromJson(response, type);
                        if(RESPONSE_SUCCESS.equals(respEntity.getCode())){
                            UpdateStatus updateStatus = respEntity.getData();
                            //showSuccessMes();
                            uploadStatus(STATUS_SUITCASE, "");
                        }
                    }
                });
    }

    /**
     *
     */
    private void reBack() {
        String url = FleetUtil.getUrl(getApplication(), R.string.reback_uri);
        OkHttpUtils.post().url(url)
                .addHeader("Authorization", FleetUtil.getCredential(getApplicationContext()))
                .addParams("logisticalCode", logisticalCode)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if(e instanceof UnknownHostException){
                            Toast.makeText(getApplicationContext(), R.string.mes_timeout_error, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(), R.string.mes_save_error, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
                        java.lang.reflect.Type type = new TypeToken<RespEntity<UpdateStatus>>() {}.getType();
                        RespEntity<UpdateStatus> respEntity = gsonHttpMessageConverter.getGson().fromJson(response, type);
                        if(RESPONSE_SUCCESS.equals(respEntity.getCode())){
                            //UpdateStatus updateStatus = respEntity.getData();
                            showSuccessMes();
                            loadTask(logisticalCode);
                        }
                        if(RESPONSE_ERROR.equals(respEntity.getCode())){
                            String errMes = respEntity.getMessage();
                            showMes(errMes);
                            //loadTask(logisticalCode);
                        }
                    }
                });
    }

    private void showSuccessMes(){
        Toast.makeText(getBaseContext(), "操作成功", Toast.LENGTH_SHORT).show();
    }

    private void showMes(String msg){
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

}
