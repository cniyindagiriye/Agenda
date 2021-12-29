package com.example.agenda;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
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

public class LoginActivity extends AppCompatActivity {

    Button btnLogin, btnSignup;
    EditText txtEmail, txtPassword;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        editor = preferences.edit();

        if (preferences.getBoolean("loggedIn", false)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);

        btnLogin.setOnClickListener(view -> login());

        btnSignup.setOnClickListener(view -> {
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
            finish();
        });

    }

    public void login() {
        String url = "https://empy-api.herokuapp.com/api/v1/login";
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Login...");
        progress.setCancelable(true);
        progress.show();
        RequestQueue queue = Volley.newRequestQueue(this);

        Map<String, String> params = new HashMap<String, String>();
        params.put("email", txtEmail.getText().toString());
        params.put("password", txtPassword.getText().toString());

        JSONObject jsonObject = new JSONObject(params);

        JsonObjectRequest rq = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progress.dismiss();
                Log.d("Response", response.toString());

                try {
                    if (response.getString("email").isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
                        return;
                    }
                    editor.putInt("userId", response.getInt("id"));
                    editor.putString("email", response.getString("email"));
                    editor.putBoolean("loggedIn", true);
                    editor.apply();
                    Toast.makeText(getApplicationContext(), "Login successfully", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
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