package com.example.viacep.view;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.viacep.recyclerview.MainAdapter;
import com.example.viacep.db.MainData;
import com.example.viacep.db.RoomDB;
import com.example.viacep.databinding.FragmentListaBinding;

import java.util.ArrayList;
import java.util.List;

public class ListaFragment extends Fragment {

    private FragmentListaBinding binding;

    List<MainData> dataList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    RoomDB database;

    MainAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListaBinding.inflate(getLayoutInflater());

        binding.pbCep.setVisibility(View.GONE);

        database = RoomDB.getInstance(getContext());
        dataList = database.mainDao().getAll();

        linearLayoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new MainAdapter((Activity) getContext(), dataList);
        binding.recyclerView.setAdapter(adapter);

        return binding.getRoot();
    }
}