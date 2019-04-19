package com.example.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import utils.BaseUrlUtils;
import utils.Configure;
import utils.ErrorCodes;
import zust.yyj.entity.User;

public class UserInfoActivity extends Activity {
    private Configure configure;
    private static final String TAG = "UserInfoActivity";
    private static final int REQUEST_SIGNUP = 0;
    private HashMap<String,String> stringHashMap;
    private final OkHttpClient client = new OkHttpClient();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        configure = (Configure) getApplication();
        String userId = configure.getToken();
        User loginUser = configure.getLoginUser();
        if (userId.trim().equals("") || loginUser == null) {//用户未登入
            Toast.makeText(getBaseContext(), "未登入", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.zhihu_toolbar_menu);
        toolbar.setNavigationIcon(R.mipmap.ic_drawer_home);
        toolbar.setTitle(R.string.my_info);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_search:
                        Toast.makeText(UserInfoActivity.this, "找我", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(UserInfoActivity.this,FindMeActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.action_info:
                        Toast.makeText(UserInfoActivity.this, "主页", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_logout:
                        Toast.makeText(UserInfoActivity.this, "已退出", Toast.LENGTH_SHORT).show();
                        configure.setLoginUser(null);
                        configure.setToken("");
                        Intent intent = new Intent(UserInfoActivity.this,LoginActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_notification:
                        Intent intent3 = new Intent(UserInfoActivity.this,IndexActivity.class);
                        startActivity(intent3);
                        break;
                }
                return true;
            }
        });

    }



}
