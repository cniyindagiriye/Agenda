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
import com.example.agenda.constants.Init;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditTaskActivity extends AppCompatActivity {

    EditText txtFirstName, txtRegNumber, txtLastName;
    Button btnSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Init.student == null) {
            finish();
        }
        setContentView(R.layout.activity_add_task);
        txtFirstName = findViewById(R.id.txtFirstName);
        txtFirstName.setText(Init.student.firstName);
        txtLastName = findViewById(R.id.txtLastName);
        txtLastName.setText(Init.student.lastName);

        txtRegNumber = findViewById(R.id.txtRegNumber);
        txtRegNumber.setText(Init.student.regNumber);

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(view -> addStudent(txtFirstName.getText().toString(), txtLastName.getText().toString(), txtRegNumber.getText().toString()));
    }

    public void addStudent(final String fn, final String ln, final String reg){
        String url = "https://empy-api.herokuapp.com/api/v1/contacts/"+Init.student.id;
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Editing...");
        progress.setCancelable(true);
        progress.show();
        RequestQueue queue = Volley.newRequestQueue(this);

        Map<String, String> params = new HashMap<String, String>();
        params.put("firstName", fn);
        params.put("lastName", ln);
        params.put("phoneNumber", reg);

        JSONObject jsonObject = new JSONObject(params);

        JsonObjectRequest rq = new JsonObjectRequest(Request.Method.PUT, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progress.dismiss();
                Log.d("Response", response.toString());
                Toast.makeText(getApplicationContext(),"Task: " + reg + " updated",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(EditTaskActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
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
        queue.add(rq);

    }
}