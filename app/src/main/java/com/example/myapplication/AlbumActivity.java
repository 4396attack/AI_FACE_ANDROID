package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import utils.BaseUrlUtils;
import utils.Configure;
import utils.OSSPathTools;
import zust.yyj.adapter.FaceAdapter;
import zust.yyj.adapter.PhotoWallAdapter;
import zust.yyj.entity.Images;
import zust.yyj.entity.User;

public class AlbumActivity extends Activity {

    private Configure configure;
    private OSS oss;
    private OkHttpClient client;
    private List<String> imageThumbUrls;

    /**
     * GridView的适配器
     */
    private PhotoWallAdapter adapter;
    /**
     * 用于展示照片墙的GridView
     */
    private GridView mPhotoWall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        configure = (Configure) getApplication();
        String userId = configure.getToken();
        imageThumbUrls = new ArrayList<>();
        mPhotoWall = (GridView) findViewById(R.id.photo_wall);
        User loginUser = configure.getLoginUser();
        if (userId.trim().equals("") || loginUser == null) {//用户未登入
            Toast.makeText(getBaseContext(), "未登入", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(AlbumActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        Log.d("userInfo",loginUser.toString());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.zhihu_toolbar_menu);
        toolbar.setNavigationIcon(R.mipmap.ic_drawer_home);
        toolbar.setTitle(R.string.album);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_search:
                        Toast.makeText(AlbumActivity.this, "找我", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(AlbumActivity.this,FindMeActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.action_info:
                        Toast.makeText(AlbumActivity.this, "主页", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(AlbumActivity.this,UserInfoActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.action_logout:
                        Toast.makeText(AlbumActivity.this, "已退出", Toast.LENGTH_SHORT).show();
                        configure.setLoginUser(null);
                        configure.setToken("");
                        Intent intent = new Intent(AlbumActivity.this,LoginActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_notification:
                        Intent intent3 = new Intent(AlbumActivity.this,IndexActivity.class);
                        startActivity(intent3);
                        break;
                }
                return true;
            }
        });
        String token = new Date().getTime() + "" + configure.getToken();
        client = new OkHttpClient();
        getOssConfig(token);//这一步完成了oss的初始化
        Images images = new Images(imageThumbUrls);
        MenuItem item = findViewById(R.id.action_find);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出程序时结束所有的下载任务
        adapter.cancelAllTasks();
    }

    private void getOssConfig(String tokenVersion) {

        Log.d("Init", "开始初始化");
//        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BaseUrlUtils.ipPCAddr + BaseUrlUtils.getOSSTokenUrl + tokenVersion)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //进行OSS的初始化
                String resp = response.body().string();
                try {

                    JSONObject json = new JSONObject(resp);
                    String configInfo = json.getString("obj");
                    Log.d("info", configInfo);
                    JSONObject params = new JSONObject(configInfo);
                    OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(params.getString("accessKeyId"), params.getString("accessKeySecret"), params.getString("securityToken"));
                    ClientConfiguration conf = new ClientConfiguration();
                    conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
                    conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
                    conf.setMaxConcurrentRequest(8); // 最大并发请求数，默认5个
                    conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
                    oss = new OSSClient(getApplicationContext(), BaseUrlUtils.getOSSEndPoint, credentialProvider, conf);
                    Log.d("Init", "OSS初始化完成");
                    getAllPhoto();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void getAllPhoto(){
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("userId",configure.getLoginUser().getId() + "");
        Request request = new Request.Builder()
                .url(BaseUrlUtils.ipPCAddr + BaseUrlUtils.getAllPhotoUrl)
                .post(formBody.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                try {
                    JSONObject json = new JSONObject(resp);
                    JSONObject obj = json.getJSONObject("obj");
                    JSONArray list = obj.getJSONArray("obj");
                    for(int i = 0;i< list.length();i++){
                        JSONObject photoInfo = list.getJSONObject(i);
                        String url = oss.presignConstrainedObjectURL(OSSPathTools.ORIGIN_BUCKET,OSSPathTools.prePhotoPath(photoInfo.getInt("userId"),photoInfo.getInt("id")),30*60);

                        imageThumbUrls.add(url);
                    }
                    updateAlbum();
                } catch (Exception e) {
                    Log.e("err","数据异常" + e);
                }
            }
        });
    }
    private void updateAlbum(){
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("t","已在主线程？" + Process.myTid());
                Images images = new Images(imageThumbUrls);
                String[] urls = imageThumbUrls.toArray(new String[imageThumbUrls.size()]);
                adapter = new PhotoWallAdapter(getApplicationContext(), 0,urls , mPhotoWall);
                adapter.initWithUrls(images);
                mPhotoWall.setAdapter(adapter);
            }
        });
    }
}
