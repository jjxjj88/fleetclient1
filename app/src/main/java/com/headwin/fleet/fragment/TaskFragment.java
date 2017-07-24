package com.headwin.fleet.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.headwin.fleet.R;
import com.headwin.fleet.activity.DetailActivity;
import com.headwin.fleet.adapter.TaskAdapter;
import com.headwin.fleet.pojo.resp.RespList;
import com.headwin.fleet.pojo.Task;
import com.headwin.fleet.util.FleetUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.springframework.http.converter.json.GsonHttpMessageConverter;

import java.net.UnknownHostException;
import java.util.ArrayList;

import okhttp3.Call;

public class TaskFragment extends Fragment {

    private static final String TAG = "TaskFragment";

    //下拉刷新控件
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView taskRecycleView;

    public TaskFragment() {
    }

    private void setActionBar(String title){
        if(getActivity() != null)
            getActivity().setTitle(title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_task, container, false);
        initView(view);
        addListener();
        loadTasks();
        return view;
    }
    /**
     * 初始化界面
     * */
    private void initView(View view){
        LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(getActivity());
        taskRecycleView = (RecyclerView)view.findViewById(R.id.recycler_task);
        taskRecycleView.setLayoutManager(linearLayoutManager);
        taskRecycleView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        swipeRefreshLayout =(SwipeRefreshLayout) view.findViewById(R.id.task_swipe_refresh) ;

    }

    /**
     * 对需要事件处理的控件增加监听事件
     * */
    private void addListener(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                loadTasks();
            }
        } );
    }

    private void loadTasks(){
        String url = FleetUtil.getUrl(getContext(), R.string.load_tasks_uri);
        String accountCode = FleetUtil.getAccountCode(getContext());
        Log.d(TAG, "loadTasks: "+ FleetUtil.getCookie(getContext()));

        OkHttpUtils.get().url(url)
                .addHeader("Authorization", FleetUtil.getCredential(getContext()))
                .addParams("accountCode", accountCode).build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if(e instanceof UnknownHostException){
                            Toast.makeText(getContext(), R.string.mes_timeout_error, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getContext(), R.string.mes_load_error, Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        RespList<Task> respList;
                        final TaskAdapter taskAdapter;

                        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
                        java.lang.reflect.Type type = new TypeToken<RespList<Task>>() {}.getType();
                        respList = gsonHttpMessageConverter.getGson().fromJson(response, type);

                        if(respList.getData() == null){
                            respList.setData(new ArrayList<Task>());
                            Toast.makeText(getContext(), R.string.mes_no_data, Toast.LENGTH_SHORT).show();
                        }

                        taskAdapter = new TaskAdapter(respList.getData());
                        taskAdapter.setOnItemClickListener(new TaskAdapter.OnRecyclerViewItemClickListener(){
                            @Override
                            public void onItemClick(View view) {
                                int position = taskRecycleView.getChildAdapterPosition(view);
                                Task task = taskAdapter.getIndexItem(position);
                                DetailActivity.actionStart(getActivity(), task);
                            }

                            @Override
                            public void onItemLongClick(View view) {

                            }
                        });
                        taskRecycleView.setAdapter(taskAdapter);
                        swipeRefreshLayout.setRefreshing(false);
                        String title = String.format("当前任务(%d)个", taskAdapter.getItemCount());
                        setActionBar(title);
                    }
                });
    }

}
