package zust.yyj.entity;

public class FacePhoto {
    private Integer photoId;
    private Integer userId;

    public FacePhoto( Integer photoId, Integer userId) {
        this.photoId = photoId;
        this.userId = userId;
    }

    public Integer getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Integer photoId) {
        this.photoId = photoId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
