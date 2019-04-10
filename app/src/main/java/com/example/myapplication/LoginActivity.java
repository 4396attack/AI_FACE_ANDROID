package com.example.myapplication;

import android.app.Application;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import utils.BaseUrlUtils;
import utils.Configure;
import utils.ErrorCodes;

public class LoginActivity extends AppCompatActivity {
    private Configure configure;
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private HashMap<String,String> stringHashMap;
    private final OkHttpClient client = new OkHttpClient();

    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;
    @BindView(R.id.link_signup) TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        configure = (Configure) getApplication();
        ButterKnife.bind(this);
        stringHashMap = new HashMap<>();
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loginPOST(v);
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }
    public void loginPOST(View view){
        new Thread(postRun).start();
    }

    /**
     * post请求线程
     */
    Runnable postRun = new Runnable() {
        @Override
        public void run() {
            enqueue();
        }
    };
    /**
     * 用OKHttp发送post请求
     */
    private void enqueue(){
        FormBody.Builder formBody = new FormBody.Builder();
                formBody.add("phone",_emailText.getText().toString())
                .add("pwd",_passwordText.getText().toString());

        Request request = new Request.Builder()
                .url(BaseUrlUtils.ipAddr + BaseUrlUtils.getLoginUrl)
                .post(formBody.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onLoginFailed("网络异常！");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respBody = response.body().string();
                try {
                    JSONObject json = new JSONObject(respBody);
                    int code = json.getInt("code");
                    String msg = json.getString("msg");
                    if(ErrorCodes.SUCCESS.equals(new Integer(code))){
                        //跳转到个人主页
                        configure.setToken("123");
                        onLoginFailed(msg);
                    }else {
                        onLoginFailed(msg);
                    }
                } catch (JSONException e) {
                    onLoginFailed("数据异常！");
                }
            }
        });
    }
    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed("fail");
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed(String msg) {
        Looper.prepare();
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
        Looper.loop();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
