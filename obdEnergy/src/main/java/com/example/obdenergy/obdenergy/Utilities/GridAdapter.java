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
    private ArrayList<Integer> imageList;



    public GridAdapter(Context context, ArrayList<Integer> imageList){
        this.context = context;
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
