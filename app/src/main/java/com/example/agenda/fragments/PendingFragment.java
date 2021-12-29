package com.example.agenda.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.agenda.R;
import com.example.agenda.adapters.PendingAdapter;
import com.example.agenda.models.Student;

import java.util.List;

public class PendingFragment extends Fragment {

    List<Student> students;
    public PendingFragment() {
        // Required empty public constructor
    }
    public PendingFragment(List<Student> students) {
        this.students = students;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending, container, false);
        PendingAdapter pendingAdapter = new PendingAdapter(students, (AppCompatActivity) requireActivity());
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));
        recyclerView.setAdapter(pendingAdapter);
        recyclerView.setVerticalScrollBarEnabled(true);
        return view;
    }
}