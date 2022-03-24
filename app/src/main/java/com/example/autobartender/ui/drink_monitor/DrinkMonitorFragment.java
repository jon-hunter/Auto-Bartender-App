package com.example.autobartender.ui.drink_monitor;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.autobartender.R;
import com.example.autobartender.ui.recipe_list.RecipeView_RVA;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DrinkMonitorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DrinkMonitorFragment extends Fragment {

    private RecyclerView rv;
    private DrinkMonitor_RVA rva;

    public DrinkMonitorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drink_monitor, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        rv = getActivity().findViewById(R.id.rvDrinkQueue);
        rv.setAdapter(new DrinkMonitor_RVA(view.getContext()));

        GridLayoutManager layoutManager = new GridLayoutManager(
                view.getContext(),
                1,
                LinearLayoutManager.VERTICAL,
                false
        );
        layoutManager.scrollToPosition(0);
        rv.setLayoutManager(layoutManager);
    }


}