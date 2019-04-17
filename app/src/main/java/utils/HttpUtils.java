package utils;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.example.myapplication.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.framed.FrameReader;
import zust.yyj.entity.OssConfigBean;

public class HttpUtils {
    private OkHttpClient client;
    private OssConfigBean bean;
    public HttpUtils(){
        this.client = new OkHttpClient();
    }
    public OssConfigBean getRespBody(String tokenVersion){
        Request request = new Request.Builder()
                .url(BaseUrlUtils.ipAddr + BaseUrlUtils.getOSSTokenUrl + tokenVersion)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
        return this.bean;
    }

    public static void main(String[] args){
        HttpUtils httpUtils = new HttpUtils();
        OssConfigBean respBody = httpUtils.getRespBody("5522372203700422672");
        System.out.println(respBody);
    }
}
