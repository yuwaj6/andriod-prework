package com.example.reneewu.simpletodo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by reneewu on 1/28/2017.
 */

public class todoItem implements Serializable {
    public int id;
    public String taskName;
    public String pri;
    public String status;
    public String dueDate;

    public todoItem() {
    }

    public todoItem(String taskName) {
        this.taskName = taskName;
    }

    // Constructor to convert JSON object into a Java class instance
    public todoItem(JSONObject object){
        try {
            this.taskName = object.getString("taskName");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Factory method to convert an array of JSON objects into a list of objects
    // User.fromJson(jsonArray);
    public static ArrayList<todoItem> fromJson(JSONArray jsonObjects) {
        ArrayList<todoItem> todoItems = new ArrayList<todoItem>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                todoItems.add(new todoItem(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return todoItems;
    }


}
