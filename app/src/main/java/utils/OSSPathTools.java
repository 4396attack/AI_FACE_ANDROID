package utils;

public class OSSPathTools {
    public static String ORIGIN_BUCKET = "ai-face-yyj";

    public static String DETAIL_BUCKET = "ai-face-detail-yyj";

    public static String prePhotoPath(Integer userId,Integer photoId){
        return "user" + userId + "/photo" + photoId + ".jpg";
    }
    /**
     * OSS路径，人脸定位图
     */
    public static String getFaceDetailPath(Integer userId,Integer photoId,Integer detailId){
        return "User" + userId +"/photo" + photoId +"/" + detailId +".jpg";
    }
    /**
     * OSS路径，用于人脸搜索
     */
    public static String getSourcePath(Integer userId,Integer sourceId){
        return "source/User" + userId + "/" + sourceId + ".jpg";
    }
}
