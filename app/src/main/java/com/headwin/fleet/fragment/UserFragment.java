package com.headwin.fleet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.headwin.fleet.R;
import com.headwin.fleet.activity.LoginActivity;
import com.headwin.fleet.activity.MainActivity;
import com.headwin.fleet.util.FleetUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;

import okhttp3.CookieJar;

public class UserFragment extends Fragment {

    public UserFragment() {
    }

    private void setActionBar(String title){
        if(getActivity() != null)
            getActivity().setTitle(title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setActionBar("我的信息");

        Button btnLogout = (Button) view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CookieJar cookieJar = OkHttpUtils.getInstance().getOkHttpClient().cookieJar();
                ((CookieJarImpl) cookieJar).getCookieStore().removeAll();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                FleetUtil.clearPersistentUser(getContext());
                startActivity(intent);
                getActivity().finish();
            }
        });

        TextView  txvLoginName = (TextView)view.findViewById(R.id.txv_login_name);
        txvLoginName.setText(FleetUtil.getLoginName(view.getContext()));

        TextView  txvMobileNo = (TextView)view.findViewById(R.id.txv_mobile_no);
        txvMobileNo.setText(FleetUtil.getMobileNo(view.getContext()));

        TextView  txvFleetName = (TextView)view.findViewById(R.id.txv_fleet_name);
        txvFleetName.setText(FleetUtil.getFleetName(view.getContext()));


    }
}
