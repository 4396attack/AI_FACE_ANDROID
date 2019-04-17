package zust.yyj.entity;

import android.graphics.Bitmap;

import java.util.Date;

public class FaceDetial {
    private Integer id;
    private Integer photoId;
    private Integer userId;
    private Date updateTime;
    private String width;
    private String height;
    private Integer deleted;
    private String pointX;
    private String pointY;
    private Integer hasCut;
    private Bitmap bitmap;

    public FaceDetial(Integer id, Integer photoId, Integer userId) {
        this.id = id;
        this.photoId = photoId;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public String getPointX() {
        return pointX;
    }

    public void setPointX(String pointX) {
        this.pointX = pointX;
    }

    public String getPointY() {
        return pointY;
    }

    public void setPointY(String pointY) {
        this.pointY = pointY;
    }

    public Integer getHasCut() {
        return hasCut;
    }

    public void setHasCut(Integer hasCut) {
        this.hasCut = hasCut;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
