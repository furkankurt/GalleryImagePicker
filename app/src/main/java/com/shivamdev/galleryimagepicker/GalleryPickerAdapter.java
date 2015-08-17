package com.shivamdev.galleryimagepicker;

import android.content.Context;
import android.content.CursorLoader;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by shivamchopra on 14/08/15.
 */
public class GalleryPickerAdapter extends RecyclerView.Adapter<GalleryPickerAdapter.MyViewHolder> {


    //define source of MediaStore.Images.Media, internal or external storage
    public static final Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public static final String[] projections = {MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.DISPLAY_NAME};
    Context context;
    LayoutInflater inflater;
    String sortOrder = MediaStore.Images.Media.DATE_ADDED + " ASC";

    LinkedHashMap<String, ArrayList<String>> folderMap = new LinkedHashMap<>();

    int count;
    private List<PhotosModel> data;


    public GalleryPickerAdapter(Context context) {
        this.context = context;

        inflater = LayoutInflater.from(context);
    }


    public void setData(List<PhotosModel> data) {
        this.data = data;
        this.notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View myView = inflater.inflate(R.layout.grid_item, parent, false);
        return new MyViewHolder(myView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final PhotosModel model = data.get(position);

        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {

                Bitmap thumb = BitmapDecoder.decodeBitmapFromFile(model.getImagePath(), 400, 400);
                //return Bitmap.createScaledBitmap(thumb, 400, 400, false);
                return thumb;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                holder.iv_grid.setImageBitmap(bitmap);
            }
        }.execute();

        holder.tv_grid.setText(model.getImageName() == null ? model.getImageBucket() : model.getImageName());
    }

    @Override
    public int getItemCount() {
        // cursor.getCount() not working so for making it work right now using myList.size() which returns total images
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        View row;
        ImageView iv_grid;
        TextView tv_grid;

        public MyViewHolder(View itemView) {
            super(itemView);
            row = itemView;
            iv_grid = (ImageView) row.findViewById(R.id.gv_image);
            tv_grid = (TextView) row.findViewById(R.id.gv_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CursorLoader cl = new CursorLoader(context, uri, projections, projections[3] + " = \"" + data.get(getLayoutPosition()).getBucketId() + "\"", null, sortOrder);
                    setData(PhotosData.getData(false, cl.loadInBackground()));
                }
            });
        }
    }
}