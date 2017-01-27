package com.example.reneewu.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;
    public final static String EDIT_ITEM = "com.example.todoapp.edititem";
    public final static String EDIT_ITEM_POS = "com.example.todoapp.edititempos";
    static final int EDIT_FORM_REQUEST = 1;  // The request code
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvItems = (ListView) findViewById(R.id.lvItems);
        //items = new ArrayList<>();
        readItems();
        itemsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);
        setupListViewListener();

    }

    public void onAddItem(View v){
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        itemsAdapter.add(itemText);
        etNewItem.setText("");
        writeItems();
    }

    private void setupListViewListener(){
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener(){
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item,
                                                   int pos,
                                                   long id){
                        items.remove(pos);
                        itemsAdapter.notifyDataSetChanged();
                        writeItems();
                        return true;
                    }
                }
        );

        lvItems.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapter,
                                                   View item,
                                                   int pos,
                                                   long id){
                        //itemsAdapter.notifyDataSetChanged();
                        //writeItems();items.remove(pos);

                        Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
                        //String item = items.get(pos).toString();
                        intent.putExtra(EDIT_ITEM, adapter.getItemAtPosition(pos).toString());
                        intent.putExtra(EDIT_ITEM_POS, pos);

                        // ref: https://developer.android.com/training/basics/intents/result.html
                        startActivityForResult(intent, EDIT_FORM_REQUEST);
                    }
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == EDIT_FORM_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String updatedItem = data.getExtras().getString(EDIT_ITEM);
                int pos = data.getIntExtra(MainActivity.EDIT_ITEM_POS,0);

                items.set(pos,updatedItem);
                itemsAdapter.notifyDataSetChanged();
                writeItems();
            }
        }
    }

    private void readItems(){
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir,"todo.txt");
        try{
            items = new ArrayList<String>(FileUtils.readLines(todoFile));
        }
        catch (IOException e){
            items = new ArrayList<>();
        }
    }

    private void writeItems(){
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir,"todo.txt");
        try{
            FileUtils.writeLines(filesDir,items);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
