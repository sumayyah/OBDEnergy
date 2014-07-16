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
    private int imageNum;
    private String imageType;
    private String[] treeList;

    public GridAdapter(Context context, int imageNum, String imageType){
        this.context = context;
        this.imageNum = imageNum;
        this.imageType = imageType;

        treeList = new String[]{"R.drawable.tree1leaf", "R.drawable.tree2leaves", "R.drawable.tree3leaves", "R.drawable.tree4leaves", "R.drawable.tree5leaves", "R.drawable.tree6leaves", "R.drawable.tree7leaves", "R.drawable.tree8leaves", "R.drawable.tree9leaves", "R.drawable.tree10leaves"};
        Console.log("New GridAdapter with treelist 1 and 3 "+treeList[0]+" "+treeList[1]);
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
            }else if(imageType.equals("TREE")) {
                imageView.setImageResource(R.drawable.leafcopy);
                setTreeImage();
            }

        return gridView;

    }

    private void setTreeImage(){
        Console.log("Gridview set tree image with number "+imageNum);


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
