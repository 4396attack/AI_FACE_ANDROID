package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
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
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import utils.BaseUrlUtils;
import utils.BitmapUtil;
import utils.Configure;
import utils.ErrorCodes;
import utils.OSSPathTools;
import zust.yyj.adapter.FaceAdapter;
import zust.yyj.adapter.PhotoWallAdapter;
import zust.yyj.entity.FaceDetial;
import zust.yyj.entity.FacePhoto;
import zust.yyj.entity.Images;
import zust.yyj.entity.User;

public class FindMeActivity extends Activity {
    private Configure configure;
    private OSS oss;
    private OkHttpClient client;
    private Integer _photoId;
    private Integer _faceNum;
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
    private List<FacePhoto> faceList;
    private List<String> imageUrls;
    /**
     * GridView的适配器
     */
    private PhotoWallAdapter adapter;
    /**
     * 用于展示照片墙的GridView
     */
    private GridView mPhotoWall;
    private Button btn_choose;

    @BindView(R.id.mache_imgView)
    ImageView _macheImg;
//    @BindView(R.id.mache_upload)
//    Button btn_choose;
    @BindView(R.id.mache_doup)
    Button btn_mache;
    @BindView(R.id.mache_probar_upload)
    ProgressBar _mache_probar;
    @BindView(R.id.mache_wait_pro)
    ProgressBar _mache_wait;
    @BindView(R.id.photo_mache)
    GridView macheResult;
    @BindView(R.id.mache_text)
    EditText macheText;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findme);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.zhihu_toolbar_menu);
        toolbar.setNavigationIcon(R.mipmap.ic_drawer_home);
        toolbar.setTitle(R.string.find_me_page);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_info:
                        Toast.makeText(FindMeActivity.this, "主页", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(FindMeActivity.this,UserInfoActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.action_logout:
                        Toast.makeText(FindMeActivity.this, "已退出", Toast.LENGTH_SHORT).show();
                        configure.setLoginUser(null);
                        configure.setToken("");
                        Intent intent = new Intent(FindMeActivity.this,LoginActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_notification:
                        Intent intent2 = new Intent(FindMeActivity.this,IndexActivity.class);
                        startActivity(intent2);
//                        break;
                }
                return true;
            }
        });
        btn_choose = findViewById(R.id.mache_upload);
        imageUrls = new ArrayList<>();
        configure = (Configure) getApplication();
        String userId = configure.getToken();
        User loginUser = configure.getLoginUser();
        if (userId.trim().equals("") || loginUser == null) {//用户未登入
            Toast.makeText(getBaseContext(), "未登入", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(FindMeActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        faceList = new ArrayList<>();
        client = new OkHttpClient();
        Log.d("userInfo",loginUser.toString());
        String token = new Date().getTime() + "" + configure.getToken();
        stringHashMap = new HashMap<>();
        getOssConfig(token);//这一步完成了oss的初始化
        btn_choose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showPopueWindow();
            }
        });
        btn_mache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findMyPhoto();
            }
        });
//        _btn_doup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                uploadChoose();
//            }
//        });
//        _btn_face.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendCutMsg();
//            }
//        });
    }

    /**
     * SD卡的访问权限
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
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

    /**
     * 初始化oss
     * @param tokenVersion
     */
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
            /**
             * 从相册中获取图片
             */
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
            _macheImg.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            macheText.setText("");
            macheText.setVisibility(View.GONE);
            btn_mache.setEnabled(true);
            macheResult.setVisibility(View.GONE);
            macheText.setVisibility(View.GONE);
            // String picturePath contains the path of selected Image
        }

        if(resultCode == RESULT_OK){
            if(requestCode == RESULT_CAMERA_IMAGE){
                /**
                 * 调用相机获取的图片
                 */
                Log.d("t","调用了相机");
                Bitmap bitmap = null ;
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mImageUri));
                    galleryAddPic(mImageUri);
                    mCurrentPhotoPath = imageFile.getAbsolutePath();
                    _macheImg.setImageBitmap(bitmap);
                    btn_mache.setEnabled(true);
                    macheResult.setVisibility(View.GONE);
                    macheText.setText("");
                    macheText.setVisibility(View.GONE);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void findMyPhoto(){
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
        _mache_probar.setVisibility(View.VISIBLE);
        _mache_probar.setProgress(0);
        //避免在上传的过程中重复点击，将控件无效化
        btn_mache.setEnabled(false);
        btn_choose.setEnabled(false);
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("userId",configure.getToken());

        Request request = new Request.Builder()
                .url(BaseUrlUtils.ipPCAddr + BaseUrlUtils.getSourcePathUrl)
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
        PutObjectRequest put = new PutObjectRequest(OSSPathTools.ORIGIN_BUCKET, OSSPathTools.getSourcePath(userId, photoId), path);
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "当前大小: " + currentSize + " 总大小: " + totalSize);
                _mache_probar.setProgress((int)(currentSize/totalSize) * 100);
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
                        _mache_wait.setVisibility(View.VISIBLE);
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
                        _mache_probar.setVisibility(View.GONE);
                        btn_mache.setEnabled(false);
                        Toast.makeText(getBaseContext(), "上传失败", Toast.LENGTH_LONG).show();

                    }
                });
            }
        });
//            task.waitUntilFinished();

    }
    //向后台发送请求，检测OSS上是否有图片，并进行图片搜索与数据库的更新
    private void confrimUpload(Integer photoId){
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("userId",configure.getToken())
                .add("sourceId",photoId + "");

        Request request = new Request.Builder()
                .url(BaseUrlUtils.ipPCAddr + BaseUrlUtils.getConfrimSourceUrl)
                .post(formBody.build())
                .build();
        Log.d("serach","确认上传开始...");
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
                        Log.d("search","确认上传成功");
//                        _uploadButton.setEnabled(true);
                        _mache_probar.setVisibility(View.GONE);
//                        _wait_pro.setVisibility(View.GONE);
//                        _btn_face.setVisibility(View.VISIBLE);
//                        _btn_face.setEnabled(true);
                        faceList.clear();
                        try {
                            JSONObject json = new JSONObject(resp);
                            if(ErrorCodes.SUCCESS.equals(json.getInt("code"))){
                                Log.d("search","准备检索图片");
//                                Toast.makeText(getBaseContext(), "上传成功", Toast.LENGTH_LONG).show();
                                startSearchPhoto();
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
    private void startSearchPhoto(){
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("userId",configure.getToken())
                .add("sourceId", _photoId+ "");

        Request request = new Request.Builder()
                .url(BaseUrlUtils.ipPCAddr + BaseUrlUtils.getSearchAllUrl)
                .post(formBody.build())
                .build();
        Log.d("search","开始检索图片");
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call,Response response) throws IOException {
                final String resps = response.body().string();
                Log.d("res",resps);
                checkMacheStatus();
            }
        });
    }

    /**
     * 轮询检测匹配进度
     */
    private void checkMacheStatus(){
//        _btn_face.setEnabled(false);
//        _mache_wait.setVisibility(View.VISIBLE);
        Log.d("status","开始轮询");
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("sourceId",_photoId + "");
        Request request = new Request.Builder()
                .url(BaseUrlUtils.ipPCAddr + BaseUrlUtils.getCheckMacheStatusUrl)
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
                        if(new Integer(1).equals(json.getJSONObject("obj").getInt("obj"))){
                            //发送请求更新UI
//                            updateAlbum();
                            getAllMachePhoto();
                        }else if (new Integer(-1).equals(json.getJSONObject("obj").getInt("obj"))){
                            Log.d("pos","无检测人脸");
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("t","已在主线程？" + Process.myTid());
                                    _mache_wait.setVisibility(View.GONE);
//                                        _rview.setVisibility(View.VISIBLE);
                                    macheText.setVisibility(View.VISIBLE);
                                    macheText.setText("未检测到符合条件的照片");
//                                        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
//                                        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//                                        _rview.setLayoutManager(layoutManager);
//                                        _rview.setAdapter(faceAdapter);
                                }
                            });

                        }else {
                            //可能处理还没完成，继续请求
                            checkMacheStatus();;
                        }
                    }
                } catch (JSONException e) {
                    Log.e("err","数据异常" + e);
                }
            }
        });
    }

    /**
     * 向后台请求数据获取所有匹配到的照片
     */
    private void getAllMachePhoto(){
        Log.d("status","开始获取最终结果");
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("sourceId",_photoId + "")
                .add("userId",configure.getLoginUser().getId()+"");
        Request request = new Request.Builder()
                .url(BaseUrlUtils.ipPCAddr + BaseUrlUtils.getAllMachePhotosUrl)
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
                    JSONArray photoList = json.getJSONObject("obj").getJSONArray("obj");
                    _faceNum = photoList.length();
                    if(_faceNum == 0){
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("t","已在主线程？" + Process.myTid());
                                Toast.makeText(FindMeActivity.this, "检索结束", Toast.LENGTH_SHORT).show();
                                _mache_wait.setVisibility(View.GONE);
                                btn_choose.setEnabled(true);
                                macheText.setVisibility(View.VISIBLE);
                                macheText.setText("本次并未匹配到合适的图片");
                            }
                        });
                    }else{
                        for(int i=0;i<photoList.length();i++){
                            JSONObject photoInfo = photoList.getJSONObject(i);
                            String url = oss.presignConstrainedObjectURL(OSSPathTools.ORIGIN_BUCKET,OSSPathTools.prePhotoPath(photoInfo.getInt("userId"),photoInfo.getInt("photoId")),30*60);
                            imageUrls.add(url);
                        }
                        updateAlbum();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void updateAlbum(){
        if(imageUrls.size() > 0){
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(FindMeActivity.this, "检索结束", Toast.LENGTH_SHORT).show();
                    Log.d("t","已在主线程？" + Process.myTid());
                    macheResult.setVisibility(View.VISIBLE);
                    _mache_wait.setVisibility(View.GONE);
                    btn_choose.setEnabled(true);
                    macheText.setVisibility(View.VISIBLE);
                    macheText.setText("本次共检索到" +_faceNum + "张符合要求的照片");
                    Images images = new Images(imageUrls);
                    String[] urls = imageUrls.toArray(new String[imageUrls.size()]);
                    Log.d("mache",urls.length+"");
                    adapter = new PhotoWallAdapter(getApplicationContext(), 0,urls , macheResult);
                    adapter.initWithUrls(images);
                    macheResult.setAdapter(adapter);
                }
            });
        }
    }
}
