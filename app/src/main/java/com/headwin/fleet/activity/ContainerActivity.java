package com.headwin.fleet.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;

import com.headwin.fleet.R;
import com.headwin.fleet.pojo.Dict;
import com.headwin.fleet.pojo.Task;
import com.headwin.fleet.pojo.Yard;
import com.headwin.fleet.util.Capital;
import com.headwin.fleet.util.DecimalDigitsInputFilter;
import com.headwin.fleet.util.DictUtil;
import com.headwin.fleet.util.FleetUtil;

import java.util.Date;

public class ContainerActivity extends AppCompatActivity {

    TableLayout layoutRoot;

    Spinner spinnerDay, spinnerHours;
    Button btnConfirm, btnCancel;
    EditText editContainerNo, editSealNo, editTare;
    Spinner  spinnerYard;

    ArrayAdapter<Yard>  adapterYard;
    ArrayAdapter<Dict> arrAdapterDay;
    ArrayAdapter<Dict>  arrAdapterHours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        initView();
        iniData();
        addListener();
    }

//    public static Intent  actionStart(Context context, String dayKey, String hoursKey, String containerNo, String sealNo, String tare, String yarCode){
//        Intent intent = new Intent(context, ContainerActivity.class);
//        intent.putExtra("dayKey", dayKey);
//        intent.putExtra("hoursKey", hoursKey);
//        intent.putExtra("containerNo", containerNo);
//        intent.putExtra("sealNo", sealNo);
//        intent.putExtra("tare", tare);
//        intent.putExtra("yardCode", yarCode);
//        context.startActivity(intent);
//        return intent;
//    }



    private void initView() {
        layoutRoot = (TableLayout)findViewById(R.id.layout_root);
        spinnerDay = (Spinner) findViewById(R.id.spinner_day);
        spinnerHours = (Spinner) findViewById(R.id.spinner_hours);

        editContainerNo = (EditText) findViewById(R.id.edit_container_no);
        editContainerNo.setTransformationMethod(new Capital());

        editSealNo = (EditText) findViewById(R.id.edit_seal_no);
        editSealNo.setTransformationMethod(new Capital());

        editTare = (EditText) findViewById(R.id.edit_tare);
        editTare.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(5,2)});

        spinnerYard = (Spinner) findViewById(R.id.spinner_yard);

        btnConfirm = (Button) findViewById(R.id.btn_confirm);
        btnCancel = (Button) findViewById(R.id.btn_cancal);

    }

    public void iniData(){
        //适配器
        arrAdapterDay= new ArrayAdapter<Dict>(this, android.R.layout.simple_spinner_item, DictUtil.getLastDay());
        //设置样式
        arrAdapterDay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinnerDay.setAdapter(arrAdapterDay);

        arrAdapterHours= new ArrayAdapter<Dict>(this, android.R.layout.simple_spinner_item, DictUtil.getHours());
        //设置样式
        arrAdapterHours.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinnerHours.setAdapter(arrAdapterHours);


        adapterYard = new ArrayAdapter<Yard>(this, android.R.layout.simple_spinner_item, DictUtil.getYards());
        //设置样式
        adapterYard.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //加载适配器
        spinnerYard.setAdapter(adapterYard);


        Intent intent = getIntent();


        String dayKey = intent.getStringExtra("dayKey");
        String hoursKey = intent.getStringExtra("hoursKey");
        String yardCode = intent.getStringExtra("yardCode");

        editContainerNo.setText(intent.getStringExtra("containerNo"));
        editSealNo.setText(intent.getStringExtra("sealNo"));
        editTare.setText(intent.getStringExtra("tare"));
        if(dayKey != null) {
            Dict dictDay = new Dict();
            dictDay.setKey(dayKey);
            spinnerDay.setSelection(arrAdapterDay.getPosition(dictDay));
        }

        if(hoursKey != null) {
            Dict dictHours = new Dict();
            dictHours.setKey(hoursKey);
            spinnerHours.setSelection(arrAdapterHours.getPosition(dictHours));
        }

        if(yardCode !=null ){
            Yard yard = new Yard();
            yard.setCode(yardCode);
            spinnerYard.setSelection(adapterYard.getPosition(yard));
        }

    }

    private void addListener() {
        layoutRoot.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                hideKeyBoard(v);
            }
        });


        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard(v);
                String cheMes = null;
                Dict dictDay = (Dict) spinnerDay.getSelectedItem();
                Dict dictHours = (Dict) spinnerHours.getSelectedItem();
                final String dayKey = dictDay.getKey();
                final String hoursKey = dictHours.getKey();

                if((cheMes = checkTime(dayKey,hoursKey)) == null){

                    String containerNo =  editContainerNo.getText().toString().toUpperCase();
                    String sealNo = editSealNo.getText().toString().toUpperCase();
                    String tare = editTare.getText().toString();
                    Yard yard = (Yard)spinnerYard.getSelectedItem();
                    String yardCode = "";
                    if(yard !=null)
                        yardCode = yard.getCode();


                    if((cheMes = FleetUtil.getCtnrVerifyMessage(containerNo)) == null &&
                            (cheMes = FleetUtil.getSealVerifyMessage(sealNo)) == null &&
                            (cheMes = FleetUtil.getTareVerifyMessage(tare)) == null &&
                            (cheMes = FleetUtil.getYardVerifyMessae(yardCode)) == null){

                        Intent intent = new Intent(ContainerActivity.this, DetailActivity.class);
                        Bundle bundle = new Bundle();

                        bundle.putString("dayKey", dayKey);
                        bundle.putString("hoursKey", hoursKey);
                        bundle.putString("containerNo", containerNo);
                        bundle.putString("sealNo", sealNo);
                        bundle.putString("tare", tare);
                        bundle.putString("yardCode", yardCode);

                        intent.putExtras(bundle);
                        ContainerActivity.this.setResult(RESULT_OK, intent);
                        ContainerActivity.this.finish();

                    }else{
                        Dialog dialog = new AlertDialog.Builder(ContainerActivity.this)
                                .setTitle(R.string.lab_prompt)
                                .setMessage(cheMes)
                                .setPositiveButton(R.string.lab_confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .create();
                        dialog.show();
                    }

                }else{
                    Dialog dialog = new AlertDialog.Builder(ContainerActivity.this)
                            .setTitle(R.string.lab_prompt)
                            .setMessage(cheMes)
                            .setPositiveButton(R.string.lab_confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    spinnerDay.setSelection(1);
                                    return;
                                }
                            })
                            .setNegativeButton(R.string.lab_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create();
                    dialog.show();
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContainerActivity.this, DetailActivity.class);
                ContainerActivity.this.setResult(RESULT_CANCELED, intent);
                ContainerActivity.this.finish();
            }
        });
    }

    private String checkTime(String dictDay, String dictHours){
        //如果是今日就判断时间是否已过
        if("0".equals(dictDay)){
            int intDictHours = Integer.parseInt(dictHours);
            Date nowDate = new Date();
            int nowHours = nowDate.getHours();
            if(nowHours > intDictHours){
                return String.format( "已过预计到厂时间，是否明日%s点?", dictHours);
            }
        }
        //dictDay
        return null;
    }

    /**
     * 隐藏键盘
     * @param v
     */
    private void hideKeyBoard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void setContainerInfo(Task task) {
        editContainerNo.setText(task.getContainerNo());
        editSealNo.setText(task.getSealNo());
        if(task.getTare() > 0)
            editTare.setText(task.getTare()+"");
        String yardCode = task.getYardCode();
        if(yardCode !=null ){
            Yard yard = new Yard();
            yard.setCode(yardCode);
            spinnerYard.setSelection(adapterYard.getPosition(yard));
        }
    }
}
