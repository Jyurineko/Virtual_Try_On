package com.example.virtual_try_on;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageListAdapter extends ArrayAdapter {

    private String[] jewelryNames;
    private Integer[] imageId;
    private Activity context;

    public ImageListAdapter(Activity context, String[] jewelryNames, Integer[] imageId){
        super(context, R.layout.row_item, jewelryNames);
        this.context = context;
        this.jewelryNames = jewelryNames;
        this.imageId = imageId;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View row = convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if (convertView==null){
            row = inflater.inflate(R.layout.row_item, null, true);
        }
        TextView textJewelry = (TextView) row.findViewById(R.id.textJewelry);
        ImageView imageJewelry = (ImageView) row.findViewById(R.id.imageJewelry);

        textJewelry.setText(jewelryNames[position]);
        imageJewelry.setImageResource(imageId[position]);
        return row;
    }
}
