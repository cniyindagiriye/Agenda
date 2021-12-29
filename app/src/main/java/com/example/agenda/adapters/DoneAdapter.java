package com.example.agenda.adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.agenda.EditTaskActivity;
import com.example.agenda.MainActivity;
import com.example.agenda.R;
import com.example.agenda.constants.Init;
import com.example.agenda.helpers.DialogBox;
import com.example.agenda.models.Student;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoneAdapter extends RecyclerView.Adapter<DoneAdapter.ViewHolder> {
    List<Student> students;
    public static AppCompatActivity activity;
    public DoneAdapter(List<Student> students, AppCompatActivity activity) {
        this.students = students;
        DoneAdapter.activity = activity;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(students.get(position));
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtRegNumber;
        CheckBox chkAction;
        ImageButton btnEdit, btnDelete;
        TextView txtCompleted, txtPending;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRegNumber = itemView.findViewById(R.id.txtRegNumber);
            chkAction = itemView.findViewById(R.id.chkAction);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            txtPending = DoneAdapter.activity.findViewById(R.id.txtPending);
            txtCompleted = DoneAdapter.activity.findViewById(R.id.txtCompleted);
            txtCompleted.setText(String.valueOf(Init.done) + " completed");
            txtPending.setText(String.valueOf(Init.pending) + " pending");
        }
        public void bind(final Student student){
            txtRegNumber.setText(student.regNumber);
            if (student.status || Init.IS_ALL) {
                txtRegNumber.setPaintFlags(txtRegNumber.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                chkAction.setChecked(true);
            } else {
                txtRegNumber.setPaintFlags(0);
                txtRegNumber.setPaintFlags( txtRegNumber.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                chkAction.setChecked(false);
            }

            chkAction.setOnClickListener(view -> {
                String url = "https://empy-api.herokuapp.com/api/v1/contacts/"+student.id;
                ProgressDialog progress = new ProgressDialog(view.getContext());
                progress.setMessage("Editing...");
                progress.setCancelable(true);
                progress.show();
                RequestQueue queue = Volley.newRequestQueue(view.getContext());

                Map<String, String> params = new HashMap<String, String>();
                params.put("firstName", student.firstName);
                params.put("lastName", student.lastName);
                params.put("phoneNumber", student.regNumber);
               String presence = " pending";
               if (chkAction.isChecked()) {
                   presence = " completed";
                   txtRegNumber.setPaintFlags(txtRegNumber.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                   params.put("photo", "true");
                   student.status = true;
               } else if (!chkAction.isChecked() || !Init.IS_ALL) {
                   txtRegNumber.setPaintFlags( txtRegNumber.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                   params.put("photo", "");
                   student.status = false;
               }
                JSONObject jsonObject = new JSONObject(params);
                String finalPresence = presence;
                JsonObjectRequest rq = new JsonObjectRequest(Request.Method.PUT, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progress.dismiss();
                        Log.d("Response", response.toString());
                        Toast.makeText(view.getContext(), student.regNumber + finalPresence,Toast.LENGTH_LONG).show();
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progress.dismiss();
                                //displaying the error in toast if occur
                                Toast.makeText(view.getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("Error :",error.toString());
                            }
                        });
                queue.add(rq);
            });

            btnEdit.setOnClickListener(view -> {
                Init.student = student;
                Intent intent = new Intent(view.getContext(), EditTaskActivity.class);
                view.getContext().startActivity(intent);
                DoneAdapter.activity.finish();
            });
            btnDelete.setOnClickListener(view -> {
                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            ProgressDialog progress = new ProgressDialog(btnDelete.getContext());
                            progress.setTitle("Deleting");
                            progress.setMessage("Please wait...");
                            progress.setCancelable(true);
                            progress.show();

                            final String JSON_URL = "https://empy-api.herokuapp.com/api/v1/contacts/"+student.id;

                            StringRequest stringRequest = new StringRequest(Request.Method.DELETE, JSON_URL, new Response.Listener<String>() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onResponse(String response) {
                                    progress.dismiss();
                                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                                    view.getContext().startActivity(intent);
                                    DoneAdapter.activity.finish();
                                }
                            },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            progress.dismiss();
                                            //displaying the error in toast if occur
                                            Toast.makeText(view.getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                            Log.e("Error :",error.toString());
                                        }
                                    });
                            RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
                            requestQueue.add(stringRequest);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                };
                DialogBox.alertBox(btnDelete.getContext(), dialogClickListener, "Are you sure to delete: " + student.regNumber + "?");
            });
        }
    }
}
