package utils;

import android.app.Application;
import android.content.Context;

import zust.yyj.entity.User;

public class Configure extends Application {
    private String token;
    private User loginUser;
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        setToken("");
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static Context getContext(){
        return mContext;
    }

    public User getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(User loginUser) {
        this.loginUser = loginUser;
    }
}
