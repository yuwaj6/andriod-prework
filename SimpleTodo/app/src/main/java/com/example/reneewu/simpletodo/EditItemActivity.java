package com.example.reneewu.simpletodo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {
    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        Intent intent = getIntent();
        String editItem = intent.getStringExtra(MainActivity.EDIT_ITEM);
        pos = intent.getIntExtra(MainActivity.EDIT_ITEM_POS,0);
        EditText etItem = (EditText) findViewById(R.id.editText);
        etItem.setText(editItem);
        etItem.setSelection(editItem.length()); // put cursor at the end of current text value
    }

    public void onEditItem(View v) {
// Create intent to deliver some kind of result data
        EditText etItem = (EditText) findViewById(R.id.editText);
        String itemText = etItem.getText().toString();
        Intent result = new Intent();
        result.putExtra(MainActivity.EDIT_ITEM, itemText);
        result.putExtra(MainActivity.EDIT_ITEM_POS, pos);
        setResult(Activity.RESULT_OK, result);
        finish();

    }
}
