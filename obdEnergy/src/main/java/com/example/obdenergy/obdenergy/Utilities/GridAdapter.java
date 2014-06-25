package com.example.obdenergy.obdenergy.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.obdenergy.obdenergy.R;

/**
 * Created by Sumayyah on 6/11/2014.
 */
public class GridAdapter extends BaseAdapter {

    private Context context;
    private int imageNum;
    private String imageType;

    public GridAdapter(Context context, int imageNum, String imageType){
        this.context = context;
        this.imageNum = imageNum;
        this.imageType = imageType;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // get layout from mobile.xml
            View gridView = inflater.inflate(R.layout.grid_contents, null);

            // set image based on selected text
            ImageView imageView = (ImageView) gridView
                    .findViewById(R.id.gridImage);

            if(imageType.equals("CARBON")){
                imageView.setImageResource(R.drawable.cloud_icon);
            }else if(imageType.equals("TREE")) imageView.setImageResource(R.drawable.leafcopy);

        return gridView;

//        if (convertView == null) convertView = inflater.inflate(R.layout.grid_contents, null);
//
//        //set image based on selected text
//            ImageView imageView = (ImageView) convertView
//                    .findViewById(R.id.gridImage);
//
//            if(imageType.equals("CARBON")){
//                imageView.setImageResource(R.drawable.cloud_icon);
//            }else if(imageType.equals("TREE")) imageView.setImageResource(R.drawable.leafcopy);
//
//        return convertView;
//        View gridView;
//
//        if (convertView == null) {
////            Console.log("Creating new grid view");
//
////            gridView = new View(context);
//
//            // get layout from mobile.xml
//            gridView = inflater.inflate(R.layout.grid_contents, null);
//
//            // set image based on selected text
//            ImageView imageView = (ImageView) gridView
//                    .findViewById(R.id.gridImage);
//
//            if(imageType.equals("CARBON")){
//                imageView.setImageResource(R.drawable.cloud_icon);
//            }else if(imageType.equals("TREE")) imageView.setImageResource(R.drawable.leafcopy);
//
//
//
//
//        } else {
////            Console.log("using old grid view");
//            gridView = (View) convertView;
//        }
//        return gridView;
    }

    @Override
    public int getCount() {
//        Console.log("Returning new number "+imageNum);
        return imageNum;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


}