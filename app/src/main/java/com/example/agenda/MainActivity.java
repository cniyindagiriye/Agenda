package com.example.agenda;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.agenda.adapters.TaskAdapter;
import com.example.agenda.constants.Init;
import com.example.agenda.models.Student;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TaskAdapter taskAdapter;
    List<Student> studentList;
    Button btnLogout;
    TextView txtPending, txtCompleted;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private static final String JSON_URL = "https://empy-api.herokuapp.com/api/v1/contacts";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        editor = preferences.edit();

        if (preferences.getString("email", "").isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        studentList = new ArrayList<>();
        taskAdapter = new TaskAdapter(studentList, this);
        loadStudents();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setVerticalScrollBarEnabled(true);
        Button btnAdd  = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddTaskActivity.class);
            startActivity(intent);
            finish();
        });

        txtCompleted = findViewById(R.id.txtCompleted);
        txtPending = findViewById(R.id.txtPending);
        txtCompleted.setText(String.valueOf(Init.done) + " completed");
        txtPending.setText(String.valueOf(Init.pending) + " pending");

        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(view -> {
            editor.clear();
            editor.apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

    }

    public void loadStudents () {
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Loading...");
        progress.setCancelable(true);
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL, new Response.Listener<String>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(String response) {
                progress.dismiss();

                try {
                    //getting the whole json object from the response
                    JSONArray contactArray = new JSONArray(response);
                    Log.d("Data ", contactArray.toString());

                    //now looping through all the elements of the json array
                    Init.done = 0;
                    Init.pending = 0;
                    studentList.clear();
                    for (int i = 0; i < contactArray.length(); i++) {

                        JSONObject contactObject = contactArray.getJSONObject(i);

                        Student student = new Student();

                        student.id = contactObject.getString("id");
                        student.regNumber = contactObject.getString("phoneNumber");
                        student.firstName = contactObject.getString("firstName");
                        student.lastName = contactObject.getString("lastName");
                        student.status = !contactObject.getString("photo").isEmpty();
                        studentList.add(student);
                        if (student.status) {
                            ++Init.done;
                        } else {
                            ++Init.pending;
                        }
                    }
                    Collections.reverse(studentList);
                    taskAdapter.setStudents(studentList);
                    taskAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    progress.dismiss();
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
                        //displaying the error in toast if occur
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Error :",error.toString());
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}