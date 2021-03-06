package com.example.reneewu.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements EditItemDialogFragment.EditItemDialogListener{
    ArrayList<todoItem> items;
    todoItemAdapter itemsAdapter;
    ListView lvItems;
    public final static String EDIT_ITEM = "com.example.todoapp.edititem";
    public final static String EDIT_ITEM_POS = "com.example.todoapp.edititempos";
    static final int EDIT_FORM_REQUEST = 1;  // The request code
    TodoDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvItems = (ListView) findViewById(R.id.lvItems);
        //items = new ArrayList<>();
        //readItems();
        databaseHelper = TodoDatabaseHelper.getInstance(this);

        // Get all posts from database
        items = databaseHelper.getAllTodos();
        itemsAdapter = new todoItemAdapter(this, items);
        lvItems.setAdapter(itemsAdapter);
        setupListViewListener();

    }

    public void onAddItem(View v){
        //EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        //String itemText = etNewItem.getText().toString();
        //todoItem newItem = databaseHelper.addTodo(new todoItem(itemText));

        //itemsAdapter.add(newItem);
        //etNewItem.setText("");

        showEditDialog(new todoItem());
    }

    private void setupListViewListener(){
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener(){
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item,
                                                   int pos,
                                                   long id){
                        todoItem deleteTarget = (todoItem) adapter.getItemAtPosition(pos);
                        databaseHelper.deleteTodo(deleteTarget);
                        //items.remove(pos);
                        itemsAdapter.remove(deleteTarget);
                        itemsAdapter.notifyDataSetChanged();
                        //writeItems();

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
                        todoItem targetItem = ((todoItem) adapter.getItemAtPosition(pos));

                        // Edit item using another activity
                        // ref: https://developer.android.com/training/basics/intents/result.html
                        //Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
                        //intent.putExtra(EDIT_ITEM, targetItem);
                        //intent.putExtra(EDIT_ITEM_POS, pos);
                        // startActivityForResult(intent, EDIT_FORM_REQUEST);

                        // Edit item using DialogFragment
                        showEditDialog(targetItem);
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
                todoItem updatedItem = (todoItem)data.getExtras().getSerializable(EDIT_ITEM);
                int pos = data.getIntExtra(MainActivity.EDIT_ITEM_POS,0);
                //todoItem updatedItem = new todoItem(updatedItemTaskName);
                items.set(pos,updatedItem);
                itemsAdapter.notifyDataSetChanged();
                //writeItems();

                // Get singleton instance of database
                //TodoDatabaseHelper databaseHelper = TodoDatabaseHelper.getInstance(this);

                // Add sample post to the database
                databaseHelper.updateTodo(updatedItem);
            }
        }
    }

    private void showEditDialog(todoItem item) {
        FragmentManager fm = getSupportFragmentManager();
        EditItemDialogFragment editItemDialogFragment = EditItemDialogFragment.newInstance(item);
        //editItemDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
        editItemDialogFragment.show(fm, "fragment_edit_item");
    }

    private void readItems(){
        // Get singleton instance of database
        TodoDatabaseHelper databaseHelper = TodoDatabaseHelper.getInstance(this);

        // Get all posts from database
        List<todoItem> allTodoItems = databaseHelper.getAllTodos();

        if(allTodoItems.size()>0){
            items = new ArrayList<todoItem>(allTodoItems);
        }
        else{
            items = new ArrayList<todoItem>();
        }

        // get from file
        /*File filesDir = getFilesDir();
        File todoFile = new File(filesDir,"todo.txt");
        try{
            //items = new ArrayList<todoItem>(FileUtils.readLines(todoFile));
            String content = FileUtils.readFileToString(todoFile);
            JSONArray jsonArray = new JSONArray(content);
            items = todoItem.fromJson(jsonArray);
        }
        catch (Exception e){
            items = new ArrayList<todoItem>();
        }*/
    }

    private void writeItems(){
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir,"todo.txt");
        try{
            FileUtils.writeStringToFile(filesDir,items.toString());

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    // 3. This method is invoked in the activity when the listener is triggered
    // Access the data result passed to the activity here
    @Override
    public void onFinishEditDialog(todoItem updatedItem) {
        if(updatedItem.id==-1){
            // add
            todoItem newItem = databaseHelper.addTodo(updatedItem);
            itemsAdapter.add(newItem);
        }
        else{
            // update database
            databaseHelper.updateTodo(updatedItem);
            items = databaseHelper.getAllTodos();
            itemsAdapter.notifyDataSetChanged();
        }
    }
}
