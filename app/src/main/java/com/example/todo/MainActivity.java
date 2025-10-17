package com.example.todo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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
    TodoDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTask = findViewById(R.id.editTextTask);
        switchUrgent = findViewById(R.id.switchUrgent);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonClearAll = findViewById(R.id.buttonClearAll);
        listViewTasks = findViewById(R.id.listViewTasks);

        dbHelper = new TodoDatabaseHelper(this);
        tasks = new ArrayList<>();
        adapter = new TodoAdapter(this, tasks);
        listViewTasks.setAdapter(adapter);

        loadTasksFromDB();
        printCursor(dbHelper.getAllTodos());

        buttonAdd.setOnClickListener(v -> {
            String text = editTextTask.getText().toString().trim();
            if (!text.isEmpty()) {
                boolean urgent = switchUrgent.isChecked();
                dbHelper.insertTodo(text, urgent ? "High" : "Normal");
                loadTasksFromDB();
                editTextTask.setText("");
                switchUrgent.setChecked(false);
            } else {
                Toast.makeText(this, getString(R.string.enter_task), Toast.LENGTH_SHORT).show();
            }
        });

        buttonClearAll.setOnClickListener(v -> {
            dbHelper.deleteAllTodos();
            loadTasksFromDB();
        });

        listViewTasks.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.dialog_title));
            builder.setMessage(getString(R.string.dialog_message));
            builder.setPositiveButton(getString(R.string.dialog_yes), (dialog, which) -> {
                int todoId = tasks.get(position).getId();
                dbHelper.deleteTodoById(todoId);
                loadTasksFromDB();
            });
            builder.setNegativeButton(getString(R.string.dialog_no), null);
            builder.show();
            return true;
        });
    }

    private void loadTasksFromDB() {
        Cursor cursor = dbHelper.getAllTodos();
        tasks.clear();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(TodoDatabaseHelper.COLUMN_ID));
                String task = cursor.getString(cursor.getColumnIndexOrThrow(TodoDatabaseHelper.COLUMN_TASK));
                String urgency = cursor.getString(cursor.getColumnIndexOrThrow(TodoDatabaseHelper.COLUMN_URGENCY));
                tasks.add(new TodoItem(id, task, urgency.equals("High")));
            } while (cursor.moveToNext());
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void printCursor(Cursor c) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Log.d("DB_INFO", "Database Version: " + db.getVersion());
        Log.d("DB_INFO", "Number of Columns: " + c.getColumnCount());

        String[] columnNames = c.getColumnNames();
        for (String col : columnNames) {
            Log.d("DB_INFO", "Column Name: " + col);
        }

        Log.d("DB_INFO", "Number of Results: " + c.getCount());

        if (c.moveToFirst()) {
            do {
                StringBuilder row = new StringBuilder();
                for (String col : columnNames) {
                    row.append(c.getString(c.getColumnIndexOrThrow(col))).append(" | ");
                }
                Log.d("DB_ROW", row.toString());
            } while (c.moveToNext());
        }
        c.close();
    }
}

