package com.example.agenda.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agenda.R;
import com.example.agenda.adapters.DoneAdapter;
import com.example.agenda.models.Student;

import java.util.List;

public class DoneFragment extends Fragment {

    List<Student> students;

    public DoneFragment() {
        // Required empty public constructor
    }

    public DoneFragment(List<Student> students) {
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
        DoneAdapter doneAdapter = new DoneAdapter(students, (AppCompatActivity) requireActivity());
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));
        recyclerView.setAdapter(doneAdapter);
        recyclerView.setVerticalScrollBarEnabled(true);
        return view;
    }
}