package com.example.agenda;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    Button btnLogin, btnSignup;
    EditText txtEmail, txtPassword;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);

        btnSignup.setOnClickListener(view -> signup());

        btnLogin.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void signup() {
        String url = "https://empy-api.herokuapp.com/api/v1/users";
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Signup...");
        progress.setCancelable(true);
        progress.show();
        RequestQueue queue = Volley.newRequestQueue(this);

        Map<String, String> params = new HashMap<String, String>();
        params.put("firstName", txtEmail.getText().toString());
        params.put("email", txtEmail.getText().toString());
        params.put("password", txtPassword.getText().toString());

        JSONObject jsonObject = new JSONObject(params);

        JsonObjectRequest rq = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progress.dismiss();
                Log.d("Response", response.toString());

                try {
                    editor.putInt("userId", response.getInt("id"));
                    editor.putBoolean("loggedIn", true);
                    editor.apply();
                    Toast.makeText(getApplicationContext(), "Signup successfully", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Signup failed", Toast.LENGTH_LONG).show();
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
        queue.add(rq);
    }
}