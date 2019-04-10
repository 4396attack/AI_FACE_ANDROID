package utils;

import android.app.Application;

public class Configure extends Application {
    private String token;

    @Override
    public void onCreate() {
        super.onCreate();
        setToken("");
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
