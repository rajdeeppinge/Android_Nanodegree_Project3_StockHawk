package com.sam_chordas.android.stockhawk.rest;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;

import java.util.ArrayList;

/**
 * Created by root on 6/18/16.
 */
public class SpinnerAdapter extends ArrayAdapter<String> {
    Context mContext;
    ArrayList<String> timePeriod;
    LayoutInflater inflater;

    public SpinnerAdapter(Context context, ArrayList<String> timePeriod) {
        super(context, R.layout.spinner_item, timePeriod);
        mContext = context;
        this.timePeriod = timePeriod;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View spinnerItem = inflater.inflate(R.layout.spinner_item, parent, false);

//        Log.d("Spinner Adapter", "Reached getView");

        TextView textView = (TextView) spinnerItem.findViewById(R.id.spinner_time_period_item);
        textView.setText(timePeriod.get(position).toString());

        return spinnerItem;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View spinnerItem = inflater.inflate(R.layout.spinner_item, parent, false);

//        Log.d("Spinner Adapter", "Reached getDropDownView");

        TextView textView = (TextView) spinnerItem.findViewById(R.id.spinner_time_period_item);
        textView.setText(timePeriod.get(position).toString());

        return spinnerItem;
    }
}
