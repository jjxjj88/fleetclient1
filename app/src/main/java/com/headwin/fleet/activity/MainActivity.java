package com.headwin.fleet.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.headwin.fleet.R;
import com.headwin.fleet.fragment.TaskFragment;
import com.headwin.fleet.fragment.TaskHistoryFragment;
import com.headwin.fleet.fragment.UserFragment;
import com.headwin.fleet.pojo.Task;
import com.headwin.fleet.util.DictUtil;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Fragment taskFragment;
    private Fragment taskHistoryFragment;
    private Fragment userFragment;


    private void replaceMainContent(Fragment fragment, Class<?> cls){
        if(fragment == null){
            try {
                fragment = (Fragment) cls.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainContent, fragment);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_task:
                    replaceMainContent(taskFragment, TaskFragment.class);
                    return true;
                case R.id.navigation_task_history:
                    replaceMainContent(taskHistoryFragment, TaskHistoryFragment.class);
                    return true;
                case R.id.navigation_user:
                    replaceMainContent(userFragment, UserFragment.class);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DictUtil.refreshYards(getApplication());
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        replaceMainContent(taskFragment, TaskFragment.class);

    }

}
