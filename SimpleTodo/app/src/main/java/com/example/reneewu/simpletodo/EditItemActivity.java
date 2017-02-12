package com.example.reneewu.simpletodo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {
    int pos;
    todoItem editItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        try {
            Intent intent = getIntent();
            //JSONObject jsonObject = new JSONObject(intent.getStringExtra(MainActivity.EDIT_ITEM));
            //todoItem editItem = new todoItem(intent.getStringExtra(MainActivity.EDIT_ITEM));
            editItem = (todoItem) intent.getExtras().getSerializable(MainActivity.EDIT_ITEM);
            pos = intent.getIntExtra(MainActivity.EDIT_ITEM_POS, 0);
            EditText etItem = (EditText) findViewById(R.id.editText);
            etItem.setText(editItem.taskName);
            etItem.setSelection(editItem.taskName.length()); // put cursor at the end of current text value
        } catch (Exception e) {

        }
    }

    public void onEditItem(View v) {
        // Create intent to deliver some kind of result data
        EditText etItem = (EditText) findViewById(R.id.editText);
        editItem.taskName = etItem.getText().toString();
        //String itemText = etItem.getText().toString();

        Intent result = new Intent();
        //result.putExtra(MainActivity.EDIT_ITEM, itemText);
        result.putExtra(MainActivity.EDIT_ITEM, editItem);
        result.putExtra(MainActivity.EDIT_ITEM_POS, pos);
        setResult(Activity.RESULT_OK, result);
        finish();
    }
}
