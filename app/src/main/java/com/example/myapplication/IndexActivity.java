package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.OSSResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import utils.BaseUrlUtils;
import utils.BitmapToBase64Util;
import utils.BitmapUtil;
import utils.Configure;
import utils.ErrorCodes;
import utils.OSSPathTools;
import zust.yyj.adapter.FaceAdapter;
import zust.yyj.entity.FaceDetial;
import zust.yyj.entity.FacePhoto;
import zust.yyj.entity.User;

public class IndexActivity extends Activity {
    private Configure configure;
    private OSS oss;
    private OkHttpClient client;
    private Integer _photoId;
    private Integer _faceNum;
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private HashMap<String, String> stringHashMap;
    public final Integer RESULT_LOAD_IMAGE = 1;
    public final Integer RESULT_CAMERA_IMAGE = 2;
    public final Integer WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 3;
    private String mCurrentPhotoPath;
    private Uri mImageUri;
    private String saveCreamePhoto = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/Screenshots";
    private File photo_file = new File(saveCreamePhoto);
    private File imageFile;
    private List<FaceDetial> faceList;


    @BindView(R.id.btn_upload)
    Button _uploadButton;
    @BindView(R.id.imgView)
    ImageView _chooseImg;
    @BindView(R.id.btn_doup)
    Button _btn_doup;
    @BindView(R.id.probar_upload)
    ProgressBar _pro_upload;
    @BindView(R.id.wait_pro)
    ProgressBar _wait_pro;
    @BindView(R.id.btn_face)
    Button _btn_face;
    @BindView(R.id.list1)
    RecyclerView _rview;
    @BindView(R.id.result_text)
    EditText _result_text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.zhihu_toolbar_menu);
        toolbar.setNavigationIcon(R.mipmap.ic_drawer_home);
        toolbar.setTitle(R.string.upload_page);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_search:
                        Toast.makeText(IndexActivity.this, "找我", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(IndexActivity.this,FindMeActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.action_info:
                        Toast.makeText(IndexActivity.this, "主页", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(IndexActivity.this,UserInfoActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.action_logout:
                        Toast.makeText(IndexActivity.this, "已退出", Toast.LENGTH_SHORT).show();
                        configure.setLoginUser(null);
                        configure.setToken("");
                        Intent intent = new Intent(IndexActivity.this,LoginActivity.class);
                        startActivity(intent);
                        break;
//                    case R.id.action_notification:
//                        Intent intent2 = new Intent(IndexActivity.this,IndexActivity.class);
//                        startActivity(intent2);
////                        break;
                }
                return true;
            }
        });
        configure = (Configure) getApplication();
        String userId = configure.getToken();
        User loginUser = configure.getLoginUser();
        if (userId.trim().equals("") || loginUser == null) {//用户未登入
            Toast.makeText(getBaseContext(), "未登入", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(IndexActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        faceList = new ArrayList<>();
        client = new OkHttpClient();
        Log.d("userInfo",loginUser.toString());
        String token = new Date().getTime() + "" + configure.getToken();
        ButterKnife.bind(this);
        stringHashMap = new HashMap<>();
        getOssConfig(token);//这一步完成了oss的初始化
        _uploadButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showPopueWindow();
            }
        });
        _btn_doup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadChoose();
            }
        });
        _btn_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPosStatus();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode,grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
            } else {
                // Permission Denied
            }
        }
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //底部选择按钮
    private void showPopueWindow() {
        View popView = View.inflate(this, R.layout.activity_popue_window, null);
        Button bt_album = (Button) popView.findViewById(R.id.btn_pop_album);
        Button bt_camera = (Button) popView.findViewById(R.id.btn_pop_camera);
        Button bt_cancel = (Button) popView.findViewById(R.id.btn_pop_cancel);
        //获取屏幕宽高
        int weright = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels * 1 / 3;

        final PopupWindow popupWindow = new PopupWindow(popView, weright, height);
        popupWindow.setAnimationStyle(R.style.anim_popup_dir);
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);

        bt_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                popupWindow.dismiss();

            }
        });
        bt_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
                popupWindow.dismiss();

            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

            }
        });

        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 50);
    }
    /**
     * 检查拍照权限
     */
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(configure, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 进入这儿表示没有权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // 提示已经禁止
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
            }
        } else {
            takeCamera(RESULT_CAMERA_IMAGE);
        }
    }
    private void takeCamera(int num) {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//打开相机的Intent
        if(takePhotoIntent.resolveActivity(getPackageManager())!=null){//这句作用是如果没有相机则该应用不会闪退，要是不加这句则当系统没有相机应用的时候该应用会闪退
            imageFile = createImageFile();//创建用来保存照片的文件

            if(imageFile!=null){
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                    /*7.0以上要通过FileProvider将File转化为Uri*/
                    mImageUri = FileProvider.getUriForFile(this,"com.zhy.android7.fileprovider",imageFile);
                }else {
                    /*7.0以下则直接使用Uri的fromFile方法将File转化为Uri*/
                    mImageUri = Uri.fromFile(imageFile);
                }
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,mImageUri);//将用于输出的文件Uri传递给相机
                startActivityForResult(takePhotoIntent, num);//打开相机
            }
        }
    }
    /**
     * 创建用来存储图片的文件，以时间来命名就不会产生命名冲突
     * @return 创建的图片文件
     */
    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_"+timeStamp+"_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = null;
        try {
            imageFile = File.createTempFile(imageFileName,".jpg",photo_file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }
    /**
     * 将拍的照片添加到相册
     * @param uri 拍的照片的Uri
     */
    private void galleryAddPic(Uri uri){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(uri);
        sendBroadcast(mediaScanIntent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            this.mCurrentPhotoPath = picturePath;
            Log.d("path",mCurrentPhotoPath);
            Log.d("path",saveCreamePhoto);
            _chooseImg.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            _btn_doup.setEnabled(true);
            _rview.setVisibility(View.GONE);
            _btn_face.setVisibility(View.GONE);
            _result_text.setVisibility(View.GONE);
            // String picturePath contains the path of selected Image
        }

        if(resultCode == RESULT_OK){
            if(requestCode == RESULT_CAMERA_IMAGE){
                Log.d("t","调用了相机");
                Bitmap bitmap = null ;
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mImageUri));
                    galleryAddPic(mImageUri);
                    mCurrentPhotoPath = imageFile.getAbsolutePath();
                    _chooseImg.setImageBitmap(bitmap);
                    _btn_doup.setEnabled(true);
                    _rview.setVisibility(View.GONE);
                    _btn_face.setVisibility(View.GONE);
                    _result_text.setText("");
                    _result_text.setVisibility(View.GONE);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
        //准备上传选中的图片
        private void uploadChoose(){
            if(mCurrentPhotoPath == null){//先判断用户是否选择了图片
                Toast.makeText(getBaseContext(), "请先选择图片", Toast.LENGTH_LONG).show();
            }else {
                FileChannel fc= null;
                try {
                    File f= new File(mCurrentPhotoPath);
                    if (f.exists() && f.isFile()){
                        FileInputStream fis= new FileInputStream(f);
                        fc= fis.getChannel();
                        if(new Integer(2*1024*1024) < fc.size()){
                            Bitmap smallBitmap = BitmapUtil.getSmallBitmap(mCurrentPhotoPath);
                            BitmapUtil.bitmapToFile(smallBitmap,f,90);
                            mCurrentPhotoPath = f.getAbsolutePath();
                            getOSSPath();
                        }else {
                            getOSSPath();
                        }
                        Log.d("size",fc.size() + "");
                    }else{
                        Log.e("err","文件不存在");
                    }
                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                } finally {
                    if (null!=fc){
                        try{
                            fc.close();
                        }catch(IOException e){
                        }
                    }
                }
            }
        }

        //请求OSS路径
        private void getOSSPath(){
            _pro_upload.setVisibility(View.VISIBLE);
            _pro_upload.setProgress(0);
            //避免在上传的过程中重复点击，将控件无效化
            _btn_doup.setEnabled(false);
            _uploadButton.setEnabled(false);
            FormBody.Builder formBody = new FormBody.Builder();
            formBody.add("userId",configure.getToken());

            Request request = new Request.Builder()
                    .url(BaseUrlUtils.ipPCAddr + BaseUrlUtils.getPhotoIdUrl)
                    .post(formBody.build())
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String resp = response.body().string();
                    Log.d("resp",resp);
                    try {
                        JSONObject json = new JSONObject(resp);
                        if(ErrorCodes.SUCCESS.equals(json.getInt("code"))){//请求成功
                            JSONObject paramInfo = json.getJSONObject("obj");
                            Log.d("info",paramInfo.toString());
                            uploadPhoto(mCurrentPhotoPath,configure.getLoginUser().getId(),paramInfo.getInt("id"));
                        }else{
                            Looper.prepare();
                            Toast.makeText(getBaseContext(), json.getString("msg"), Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }
                    } catch (JSONException e) {
                        Looper.prepare();
                        Toast.makeText(getBaseContext(), "数据异常", Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                }
            });

        }
        //OSS上传图片
        public void uploadPhoto(String path, Integer userId, final Integer photoId){
            Log.d("up", "开始上传");
            Log.d("path", path);
            PutObjectRequest put = new PutObjectRequest(OSSPathTools.ORIGIN_BUCKET, OSSPathTools.prePhotoPath(userId, photoId), path);
            put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                @Override
                public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                    Log.d("PutObject", "当前大小: " + currentSize + " 总大小: " + totalSize);
                    _pro_upload.setProgress((int)(currentSize/totalSize) * 100);
                }
            });

            OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                @Override
                public void onSuccess(PutObjectRequest request, PutObjectResult result) {
//                    Looper.prepare();
//                    _pro_upload.setVisibility(View.GONE);
//                    btnStat(true);
                    Log.d("PutObject", "UploadSuccess");
                    Log.d("ETag", result.getETag());
                    Log.d("RequestId", result.getRequestId());
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("t","已在主线程中？" + Process.myTid());
//                            _pro_upload.setVisibility(View.GONE);
                            _wait_pro.setVisibility(View.VISIBLE);
                            _photoId = photoId;
                            confrimUpload(photoId);
//                            _btn_doup.setEnabled(false);
//                            Toast.makeText(getBaseContext(), "上传成功", Toast.LENGTH_LONG).show();

                        }
                    });
                }

                @Override
                public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
//                    _pro_upload.setVisibility(View.GONE);
//                    btnStat(true);
                    // 请求异常
                    if (clientException != null) {
                        // 本地异常如网络异常等
                        clientException.printStackTrace();
                    }
                    if (serviceException != null) {
                        // 服务异常
                        Log.e("ErrorCode", serviceException.getErrorCode());
                        Log.e("RequestId", serviceException.getRequestId());
                        Log.e("HostId", serviceException.getHostId());
                        Log.e("RawMessage", serviceException.getRawMessage());
                    }
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("t","已在主线程中？" + Process.myTid());
                            _pro_upload.setVisibility(View.GONE);
                            _btn_doup.setEnabled(false);
                            Toast.makeText(getBaseContext(), "上传失败", Toast.LENGTH_LONG).show();

                        }
                    });
                }
            });
//            task.waitUntilFinished();

        }

        //向后台发送请求，检测OSS上是否有图片，并进行人脸检索与数据库的更新
        private void confrimUpload(final Integer photoId){
            FormBody.Builder formBody = new FormBody.Builder();
            formBody.add("userId",configure.getToken())
                    .add("id",photoId + "");

            Request request = new Request.Builder()
                    .url(BaseUrlUtils.ipPCAddr + BaseUrlUtils.getConfrimPhotoUrl)
                    .post(formBody.build())
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String resp = response.body().string();
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            _uploadButton.setEnabled(true);
                            _pro_upload.setVisibility(View.GONE);
                            _wait_pro.setVisibility(View.GONE);
                            _btn_face.setVisibility(View.VISIBLE);
                            _btn_face.setEnabled(true);
                            faceList.clear();
                            try {
                                JSONObject json = new JSONObject(resp);
                                if(ErrorCodes.SUCCESS.equals(json.getInt("code"))){
                                    Toast.makeText(getBaseContext(), "上传成功", Toast.LENGTH_LONG).show();
                                    /**
                                     * 发送人脸定位请求
                                     */
                                    sendCutMsg();
                                }else{
                                    Toast.makeText(getBaseContext(), json.getString("msg"), Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(getBaseContext(), "数据异常", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }
            });
        }

    /**
     * 发送人脸定位监测请求
     */
    private void sendCutMsg(){
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("photoId",_photoId + "");
        Request request = new Request.Builder()
                .url(BaseUrlUtils.ipPCAddr + BaseUrlUtils.getSendPosMsgUrl)
                .post(formBody.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //此时后台已经在处理中，我们要轮询去请求服务器，看人脸的定位是否已经处理完成
                Log.d("time","到这了吗？");
//                checkPosStatus();
            }
        });
    }
    /**
     * 轮询请求，判断人脸定位的处理状态
     */
    private void checkPosStatus(){
        _btn_face.setEnabled(false);
        _wait_pro.setVisibility(View.VISIBLE);
        Log.d("status","开始轮询");
            FormBody.Builder formBody = new FormBody.Builder();
            formBody.add("photoId",_photoId + "");
            Request request = new Request.Builder()
                    .url(BaseUrlUtils.ipPCAddr + BaseUrlUtils.getCheckStatusUrl)
                    .post(formBody.build())
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String resp = response.body().string();
                    Log.d("reps",resp);
                    try {
                        JSONObject json = new JSONObject(resp);
                        // status = 1 或者 = -1 都是定位已经完成，不需要再去轮询请求了
                        if(ErrorCodes.SUCCESS.equals(json.getInt("code"))){
                            if(new Integer(1).equals(json.getJSONObject("obj").getInt("status"))){
                                //发送请求更新UI
                                getAllFaceInfo(_photoId);
                            }else if (new Integer(-1).equals(json.getJSONObject("obj").getInt("status"))){
                                Log.d("pos","无检测人脸");
                                Handler mainHandler = new Handler(Looper.getMainLooper());
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d("t","已在主线程？" + Process.myTid());
                                        FaceAdapter faceAdapter = new FaceAdapter(faceList);
                                        _wait_pro.setVisibility(View.GONE);
//                                        _rview.setVisibility(View.VISIBLE);
                                        _result_text.setVisibility(View.VISIBLE);
                                        _result_text.setText("未检测到人脸信息");
//                                        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
//                                        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//                                        _rview.setLayoutManager(layoutManager);
//                                        _rview.setAdapter(faceAdapter);
                                    }
                                });

                            }else {
                                //可能处理还没完成，继续请求
                                checkPosStatus();;
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("err","数据异常" + e);
                    }
                }
            });
        }
/**
 * 获取所有人脸位置信息
 *
 */
private void getAllFaceInfo(Integer photoId){
    if(_photoId == null){
        return;
    }
    FormBody.Builder formBody = new FormBody.Builder();
    formBody.add("photoId",photoId + "");
    Request request = new Request.Builder()
            .url(BaseUrlUtils.ipPCAddr + BaseUrlUtils.getAllFacesPosUrl)
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
                if(ErrorCodes.SUCCESS.equals(json.getInt("code"))){
                    JSONObject obj = json.getJSONObject("obj");
                    _faceNum = obj.getInt("num");//人脸数量
                    if (_faceNum > 0){
                        JSONArray faces = obj.getJSONArray("faces");
                        for(int i =0 ;i<faces.length();i++){
                            JSONObject face = faces.getJSONObject(i);
                            FaceDetial fd = new FaceDetial(face.getInt("id"),face.getInt("photoId"),face.getInt("userId"));
                            faceList.add(fd);
                        }
                        showFaceDetail();
                    }
                }else{
                    Log.d("result",json.getString("msg"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    });
}
    /**
     * 更新UI
     */
    private void showFaceDetail() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (FaceDetial fd : faceList){
                        String urlStr = oss.presignConstrainedObjectURL(OSSPathTools.DETAIL_BUCKET,OSSPathTools.getFaceDetailPath(fd.getUserId(),fd.getPhotoId(),fd.getId()),30*60);
                        Log.d("url",urlStr);
                        URL url = new URL(urlStr);
                        Bitmap bitmap = BitmapFactory.decodeStream(url.openStream());
                        fd.setBitmap(bitmap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("t","已在主线程？" + Process.myTid());
                        FaceAdapter faceAdapter = new FaceAdapter(faceList);
                        _wait_pro.setVisibility(View.GONE);
                        _rview.setVisibility(View.VISIBLE);
                        _result_text.setVisibility(View.VISIBLE);
                        _result_text.setText("一共检测到" +_faceNum + "张人脸");
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                        _rview.setLayoutManager(layoutManager);
                        _rview.setAdapter(faceAdapter);
                    }
                });
            }
        }).start();
    }
    }
