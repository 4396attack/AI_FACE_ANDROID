package utils;

public class BaseUrlUtils {
    public  final static String ipAddr = "http://10.0.2.2:8080/boot";

    public final static String ipPCAddr = "http://118.31.44.195:8080/boot";

    public static String getLoginUrl = "/user/login";

    public static String getRegistUrl = "/user/createUser";

    public static String getOSSTokenUrl = "/policyOSS/getALiYunOSSToken?tokenName=";

    public static String getPhotoIdUrl = "/aiface/preUpload";

    public static String getConfrimPhotoUrl = "/aiface/checkUpload";

    public static String getSendPosMsgUrl = "/cutPhoto/sendMsg";

    public static String getCheckStatusUrl = "/cutPhoto/checkStatus";

    public static String getAllFacesPosUrl = "/cutPhoto/get/all/face/pos";

    public static String getAllPhotoUrl = "/aiface/get/all/photo";

    public static String getSourcePathUrl = "/find/choose/pic/source";

    public static String getConfrimSourceUrl = "/find/check/source/upload";

    public static String getSearchAllUrl = "/find/start/search/all";

    public static String getCheckMacheStatusUrl = "/find/check/mache/status";

    public static String getAllMachePhotosUrl = "/find/get/all/mache/photos";

    public static String getOSSEndPoint = "http://oss-cn-hangzhou.aliyuncs.com";
}
