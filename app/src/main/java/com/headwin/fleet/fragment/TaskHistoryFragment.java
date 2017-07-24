package com.headwin.fleet.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.headwin.fleet.adapter.TaskHistoryAdapter;
import com.headwin.fleet.pojo.resp.PageImpl;
import com.headwin.fleet.pojo.resp.RespPage;
import com.headwin.fleet.pojo.Task;
import com.headwin.fleet.util.FleetUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.springframework.http.converter.json.GsonHttpMessageConverter;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class TaskHistoryFragment extends Fragment {

    private static final String TAG = "TaskHistoryFragment";

    //下拉刷新控件
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayoutManager linearLayoutManager;
    RecyclerView taskHistoryRecycleView;

    TaskHistoryAdapter taskHistoryAdapter;

    int page = 0;
    int maxPage = 0;
    int lastVisibleItem;
    int totalElements = 0;

    public TaskHistoryFragment() {
    }

    private void setActionBar(String title){
        if(getActivity() != null)
            getActivity().setTitle(title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_task_history, container, false);
        initView(view);
        addListener();
        loadTasks(page);
        return view;
    }
    /**
     * 初始化界面
     * */
    private void initView(View view){
        linearLayoutManager =  new LinearLayoutManager(getActivity());
        taskHistoryRecycleView = (RecyclerView)view.findViewById(R.id.recycler_task_history);
        taskHistoryRecycleView.setLayoutManager(linearLayoutManager);
        taskHistoryRecycleView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));

        swipeRefreshLayout =(SwipeRefreshLayout) view.findViewById(R.id.task_history_swipe_refresh) ;

    }

    /**
     * 对需要事件处理的控件增加监听事件
     * */
    private void addListener(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                page = 0;
                loadTasks(page);
                swipeRefreshLayout.setRefreshing(false);
            }
        } );

        taskHistoryRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 1  >= linearLayoutManager.getItemCount()) {
                    if(++page < maxPage){
                        loadTasks(page);
                    }else{
                        Toast.makeText(getContext(), R.string.mes_no_more, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    private void loadTasks(final int page){
        String url = FleetUtil.getUrl(getContext(), R.string.load_tasks_history_uri);
        String accountCode = FleetUtil.getAccountCode(getContext());
        Log.d(TAG, "=============="+page);

        OkHttpUtils.get().url(url)
                .addHeader("Authorization", FleetUtil.getCredential(getContext()))
                .addParams("accountCode", accountCode)
                .addParams("page", ""+page)
                .addParams("size", "5")
                .build()
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

                        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
                        java.lang.reflect.Type type = new TypeToken<RespPage<Task>>() {}.getType();
                        RespPage<Task> respPage = gsonHttpMessageConverter.getGson().fromJson(response, type);

                        PageImpl<Task> pageImpl = respPage.getData();
                        List<Task> mores = new ArrayList<Task>();

                        if(pageImpl != null && pageImpl.getContent() != null){
                            mores = pageImpl.getContent();
                            maxPage = pageImpl.getTotalPages();
                            totalElements = pageImpl.getTotalElements();
                        }

                        if(taskHistoryAdapter == null){
                            taskHistoryAdapter = new TaskHistoryAdapter(mores);
                            taskHistoryRecycleView.setAdapter(taskHistoryAdapter);

                        }else{
                            if(page == 0) taskHistoryAdapter.clearTasks();
                            taskHistoryAdapter.addMoreTasks(mores);
                        }
                        taskHistoryAdapter.setOnItemClickListener(new TaskHistoryAdapter.OnRecyclerViewItemClickListener(){
                            @Override
                            public void onItemClick(View view) {
                                int position = taskHistoryRecycleView.getChildAdapterPosition(view);
                                Task task = taskHistoryAdapter.getIndexItem(position);
                                DetailActivity.actionStart(getActivity(), task);
                            }

                            @Override
                            public void onItemLongClick(View view) {

                            }
                        });
                        String title = String.format("你总共完成了(%d)个任务", totalElements);
                        setActionBar(title);

                    }
                });
    }

}
