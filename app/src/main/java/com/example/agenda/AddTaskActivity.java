package com.example.agenda;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddTaskActivity extends AppCompatActivity {

    Button btnSave, btnBack;
    EditText txtFirstName, txtLastName, txtRegNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        txtFirstName = findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        txtRegNumber = findViewById(R.id.txtRegNumber);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(view -> addStudent(txtFirstName.getText().toString(), txtLastName.getText().toString(), txtRegNumber.getText().toString()));
    }

    public void addStudent(final String fn, final String ln, final String reg){
        String url = "https://empy-api.herokuapp.com/api/v1/contacts";
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Adding...");
        progress.setCancelable(true);
        progress.show();
        RequestQueue queue = Volley.newRequestQueue(this);

        Map<String, String> params = new HashMap<String, String>();
        params.put("firstName", fn);
        params.put("lastName", ln);
        params.put("phoneNumber", reg);
        params.put("photo", "true");

        JSONObject jsonObject = new JSONObject(params);

        JsonObjectRequest rq = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progress.dismiss();
                Log.d("Response", response.toString());
                Toast.makeText(getApplicationContext(),"TAsk: " + reg + " added",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AddTaskActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occur
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Error :",error.toString());
                    }
                });
        queue.add(rq);

    }
}