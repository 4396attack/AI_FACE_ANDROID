package utils;

import android.util.Log;

import java.util.TimerTask;

public class Task extends TimerTask {

    @Override
    public void run() {
        Log.d("task","开始执行timer定时任务");
    }
}
