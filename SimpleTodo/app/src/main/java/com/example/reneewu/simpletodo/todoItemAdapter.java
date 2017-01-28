package com.example.reneewu.simpletodo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by reneewu on 1/28/2017.
 */


public class todoItemAdapter extends ArrayAdapter<todoItem> {
    public todoItemAdapter(Context context, ArrayList<todoItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        todoItem item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_todo, parent, false);
        }
        // Lookup view for data population
        TextView taskName = (TextView) convertView.findViewById(R.id.taskName);
        // Populate the data into the template view using the data object
        taskName.setText(item.taskName);
        // Return the completed view to render on screen
        return convertView;
    }
}