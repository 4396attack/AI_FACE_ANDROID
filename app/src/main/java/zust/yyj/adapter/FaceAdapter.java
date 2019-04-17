package zust.yyj.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.myapplication.R;

import java.util.List;

import zust.yyj.entity.FaceDetial;

public class FaceAdapter extends RecyclerView.Adapter<FaceAdapter.ViewHolder> {
    private List<FaceDetial> faces;
    public FaceAdapter(List<FaceDetial> faceList){
        this.faces = faceList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.faces_item,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        FaceDetial news = faces.get(i);
        viewHolder.newsImage.setImageBitmap(news.getBitmap());
    }

    @Override
    public int getItemCount() {
        return faces.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView newsImage;

        public ViewHolder(View itemView) {
            super(itemView);

            newsImage = itemView.findViewById(R.id.newsPic);
        }
    }
}
