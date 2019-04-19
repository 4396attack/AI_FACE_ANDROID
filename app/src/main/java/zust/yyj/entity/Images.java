package zust.yyj.entity;

import java.util.List;

public class Images {
    private List<String> imageThumbUrls ;

    public Images(List<String> imageThumbUrls) {
        this.imageThumbUrls = imageThumbUrls;
    }

    public List<String> getImageThumbUrls() {
        return imageThumbUrls;
    }

    public void setImageThumbUrls(List<String> imageThumbUrls) {
        this.imageThumbUrls = imageThumbUrls;
    }
}
