package utils;

import android.app.Application;
import android.content.Context;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;

/**
 * OSS操作的封装类
 */
public class OssServiceUtil {
    private OSS oss;
    private String accessKeyId;
    private String bucketName;
    private String accessKeySecret;
    private String endPoint;
    private Context context;
    private ProgressCallback progressCallback;

    public OssServiceUtil(String accessKeyId, String bucketName, String accessKeySecret, String endPoint, Context context) {
        this.accessKeyId = accessKeyId;
        this.bucketName = bucketName;
        this.accessKeySecret = accessKeySecret;
        this.endPoint = endPoint;
        this.context = context;

    }

    public void initOSSClient(){

    }

    public ProgressCallback getProgressCallback() {
        return progressCallback;
    }

    public void setProgressCallback(ProgressCallback progressCallback) {
        this.progressCallback = progressCallback;
    }

    public interface ProgressCallback {
        void onProgressCallback(double progress);
    }

}
