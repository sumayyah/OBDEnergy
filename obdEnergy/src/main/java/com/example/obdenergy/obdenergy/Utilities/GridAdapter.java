package com.example.obdenergy.obdenergy.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.obdenergy.obdenergy.R;

import java.util.ArrayList;

/**
 * Created by Sumayyah on 6/11/2014.
 */
public class GridAdapter extends BaseAdapter {

    private Context context;
//    private int imageNum;
//    private String imageType;
    private ArrayList<Integer> imageList;

//    public GridAdapter(Context context, int imageNum, String imageType){
//        this.context = context;
//        this.imageNum = imageNum;
//        this.imageType = imageType;
//
//        imageList = new String[]{"R.drawable.tree1leaf", "R.drawable.tree2leaves", "R.drawable.tree3leaves", "R.drawable.tree4leaves", "R.drawable.tree5leaves", "R.drawable.tree6leaves", "R.drawable.tree7leaves", "R.drawable.tree8leaves", "R.drawable.tree9leaves", "R.drawable.tree10leaves"};
//    }

    public GridAdapter(Context context, ArrayList<Integer> imageList){
        this.context = context;
//        this.imageType = imageType;
        this.imageList = imageList;
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

            imageView.setImageResource(imageList.get(position));

//            if(imageType.equals("CARBON")){
//                imageView.setImageResource(R.drawable.cloud_icon);
//            }else if(imageType.equals("TREE")) {
////                Console.log("setting leaf image at " + position+" with "+imageList.get(position));
//                imageView.setImageResource(imageList.get(position));
//            }

        return gridView;

    }

    @Override
    public int getCount() {
//        Console.log("Returning count " + imageList.size());
//        return imageNum;
        return imageList.size();
    }

    @Override
    public Object getItem(int position) {
        Console.log("Get Item, position is "+position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


}
