package utils;

public class OSSPathTools {
    public static String ORIGIN_BUCKET = "ai-face-yyj";

    public static String DETAIL_BUCKET = "ai-face-detail-yyj";

    public static String prePhotoPath(Integer userId,Integer photoId){
        return "user" + userId + "/photo" + photoId + ".jpg";
    }
}
