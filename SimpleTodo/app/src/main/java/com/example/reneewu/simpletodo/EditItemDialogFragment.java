package com.example.reneewu.simpletodo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

/**
 * Created by reneewu on 2/4/2017.
 */

public class EditItemDialogFragment extends DialogFragment {
    private static todoItem targetItem;
    private EditText mTaskName;
    private DatePicker mDatePicker;
    // the listener interface with a method passing back data result.
    public interface EditItemDialogListener {
        void onFinishEditDialog(todoItem item);
    }

    public EditItemDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static EditItemDialogFragment newInstance(todoItem item) {
        EditItemDialogFragment frag = new EditItemDialogFragment();
        Bundle args = new Bundle();
        targetItem = item;
        args.putInt("id", item.id);
        args.putString("taskname", item.taskName);
        args.putString("dueDate", item.dueDate);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_item, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mTaskName = (EditText) view.findViewById(R.id.txt_taskName);
        mDatePicker = (DatePicker) view.findViewById(R.id.datePicker);

        // Fetch arguments from bundle and set title
        int id = getArguments().getInt("id");
        String taskName = getArguments().getString("taskname", "Enter Task Name");
        String dueDate = getArguments().getString("dueDate");

        if(id != -1){
            mTaskName.setText(taskName);
            mTaskName.requestFocus();

            String[] separatedDueDate = dueDate.split("-");

            // Note- 11:December for updateDate:month
            // Ref: http://stackoverflow.com/questions/5817883/setting-time-and-date-to-date-picker-and-time-picker-in-android
            mDatePicker.updateDate(Integer.parseInt(separatedDueDate[0]),Integer.parseInt(separatedDueDate[1])-1,Integer.parseInt(separatedDueDate[2]));
        }

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Button btnSave = (Button) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener()
        {
            // Perform button logic
            @Override public void onClick(View v){
                EditItemDialogListener listener = (EditItemDialogListener) getActivity();
                String date =  Integer.toString(mDatePicker.getYear()) + '-' + Integer.toString(mDatePicker.getMonth()+1) + '-' +
                        Integer.toString(mDatePicker.getDayOfMonth());
                todoItem updatedItem = targetItem;
                updatedItem.taskName = mTaskName.getText().toString();
                updatedItem.dueDate = date;
                listener.onFinishEditDialog( updatedItem );
                dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }
}
