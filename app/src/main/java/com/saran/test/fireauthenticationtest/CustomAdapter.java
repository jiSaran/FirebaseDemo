package com.saran.test.fireauthenticationtest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by core I5 on 1/11/2017.
 */

public class CustomAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> list;

    public CustomAdapter(Context context, List<String> list) {
        super(context,R.layout.adapter_page,list);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.adapter_page,parent,false);
        TextView textView = (TextView)row.findViewById(R.id.adptxt);
        textView.setText(this.list.get(position));
        return row;
    }
}
