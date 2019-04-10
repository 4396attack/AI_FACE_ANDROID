package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;




import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import utils.BaseUrlUtils;
import utils.Configure;
import utils.ErrorCodes;
import utils.JsonTools;
import zust.yyj.entity.User;

public class SignupActivity extends AppCompatActivity {
    private Configure configure;
    private static final String TAG = "SignupActivity";
    private JsonTools jsonTools;
    private OkHttpClient client = new OkHttpClient();


    @BindView(R.id.input_name) EditText _nameText;
    @BindView(R.id.input_age) EditText _ageText;
//    @BindView(R.id.input_realname) EditText _emailText;
    @BindView(R.id.input_realname) EditText _realnameText;
    @BindView(R.id.input_phone) EditText _phoneText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.link_login) TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        configure = (Configure) getApplication();
        ButterKnife.bind(this);
        jsonTools = new JsonTools();
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginPOST(v);
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }
    public void loginPOST(View view){
        String token = configure.getToken();
        if(token == null || token.trim().equals("")){//尚未登入
            Toast.makeText(getBaseContext(), "还未登入！", Toast.LENGTH_LONG).show();
            _signupButton.setEnabled(true);
            return;
        }
        if(!validate()){
            Toast.makeText(getBaseContext(), "请将信息填写完整！", Toast.LENGTH_LONG).show();
            _signupButton.setEnabled(true);
            return;
        }
        _signupButton.setEnabled(false);
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
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
        String name = _nameText.getText().toString();
        Integer age = Integer.parseInt(_ageText.getText().toString());
        String realName = _realnameText.getText().toString();
        String phone = _phoneText.getText().toString();
        String pwd = _passwordText.getText().toString();
        User user = new User(name,realName,age,phone,pwd);
        String jsonStr = jsonTools.objToJson(user);
//        String json = "{\"name\" : \"aaa\" , \"age\" : \"20\"}";
//        RequestBody body = RequestBody.create(JSON,json);
        RequestBody body =new FormBody.Builder()
                .add("name",user.getName())
                .add("age",user.getAge() + "")
                .add("realName",user.getRealName())
                .add("phone",user.getPhone())
                .add("pwd",user.getPwd())
                .add("deleted",user.getDeleted() + "")
                .build();
        Request request = new Request.Builder()
                .url(BaseUrlUtils.ipAddr + BaseUrlUtils.getRegistUrl)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onSignupFailed("网络异常！");
            }

            @Override
            public void onResponse(Call call,Response response) throws IOException {
                String respBody = response.body().string();
                try {
                    JSONObject json = new JSONObject(respBody);
                    int code = json.getInt("code");
                    String msg = json.getString("msg");
                    if(ErrorCodes.SUCCESS.equals(new Integer(code))){
                        //跳转到个人主页
                        onSignupFailed(msg);
                    }else {
                        onSignupFailed(msg);
                    }
                } catch (JSONException e) {
                    onSignupFailed("数据异常！");
                }
            }
        });
    }
    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed("请填写完整信息！");
            return;
        }

        _signupButton.setEnabled(false);

//        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
//                R.style.AppTheme_Dark_Dialog);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Creating Account...");
//        progressDialog.show();
//
//        String name = _nameText.getText().toString();
//        String age = _ageText.getText().toString();
//        String email = _realnameText.getText().toString();
//        String mobile = _phoneText.getText().toString();
//        String password = _passwordText.getText().toString();
//        String reEnterPassword = _reEnterPasswordText.getText().toString();
//
//        // TODO: Implement your own signup logic here.
//
//        new android.os.Handler().postDelayed(
//                new Runnable() {
//                    public void run() {
//                        // On complete call either onSignupSuccess or onSignupFailed
//                        // depending on success
//                        onSignupSuccess();
//                        // onSignupFailed();
//                        progressDialog.dismiss();
//                    }
//                }, 3000);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed(String msg) {
        Looper.prepare();
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
        Looper.loop();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String age = _ageText.getText().toString();
        String realName = _realnameText.getText().toString();
        String phone = _phoneText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (age.isEmpty()) {
            _ageText.setError("Enter Valid Age");
            valid = false;
        } else {
            _ageText.setError(null);
        }


        if (realName.isEmpty()) {
            _realnameText.setError("enter a valid realName");
            valid = false;
        } else {
            _realnameText.setError(null);
        }

        if (phone.isEmpty() || phone.length() < 5) {
            _phoneText.setError("Enter Valid Mobile Number");
            valid = false;
        } else {
            _phoneText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }
}
