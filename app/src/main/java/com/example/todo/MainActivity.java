package com.example.todo;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText editTextTask;
    SwitchCompat switchUrgent;
    Button buttonAdd, buttonClearAll;
    ListView listViewTasks;
    ArrayList<TodoItem> tasks;
    TodoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTask = findViewById(R.id.editTextTask);
        switchUrgent = findViewById(R.id.switchUrgent);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonClearAll = findViewById(R.id.buttonClearAll);
        listViewTasks = findViewById(R.id.listViewTasks);

        tasks = new ArrayList<>();
        adapter = new TodoAdapter(this, tasks);
        listViewTasks.setAdapter(adapter);

        buttonAdd.setOnClickListener(v -> {
            String text = editTextTask.getText().toString().trim();
            if (!text.isEmpty()) {
                tasks.add(new TodoItem(text, switchUrgent.isChecked()));
                adapter.notifyDataSetChanged();
                editTextTask.setText("");
                switchUrgent.setChecked(false);
            } else {
                Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show();
            }
        });

        buttonClearAll.setOnClickListener(v -> {
            tasks.clear();
            adapter.notifyDataSetChanged();
        });

        listViewTasks.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.dialog_title));
            builder.setMessage(getString(R.string.dialog_message) + position);
            builder.setPositiveButton(getString(R.string.dialog_yes), (dialog, which) -> {
                tasks.remove(position);
                adapter.notifyDataSetChanged();
            });
            builder.setNegativeButton(getString(R.string.dialog_no), null);
            builder.show();
            return true;
        });
    }
}
