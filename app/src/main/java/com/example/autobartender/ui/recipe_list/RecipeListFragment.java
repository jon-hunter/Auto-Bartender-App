package com.example.autobartender.ui.recipe_list;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autobartender.R;
import com.example.autobartender.databinding.FragmentRecipeListBinding;
import com.example.autobartender.ui.recipe_editor.RecipeEditorActivity;
import com.example.autobartender.utils.RecipeManager;
import com.example.autobartender.utils.RecipeManager.RecipeSortOrder;


public class RecipeListFragment extends Fragment {
    private static final String TAG = RecipeListFragment.class.getSimpleName();

    private FragmentRecipeListBinding binding;

    private RecipeView_RVA rva;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.binding = FragmentRecipeListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: launched");

        // setup sort order spinner
        Spinner sortOrderSpinner = getActivity().findViewById(R.id.spinner_sort_order);
        sortOrderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: sort order chosen" + RecipeSortOrder.values()[position]);
                RecipeManager.setSortOrder(RecipeSortOrder.values()[position]);
                rva.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(),
                R.layout.spinner_item,
                RecipeManager.getSortOrderNames(getContext())
        );
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sortOrderSpinner.setAdapter(adapter);

        // setup new recipe button
        Button btnEdit = getActivity().findViewById(R.id.btn_new_recipe);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { launchEditActivity(); }
        });

        // Init the recyclerview
        RecyclerView rv = getActivity().findViewById(R.id.rev_recipe_list);
        this.rva = new RecipeView_RVA(view.getContext());
        rv.setAdapter(this.rva);

        GridLayoutManager layoutManager = new GridLayoutManager(
                view.getContext(),
                1,
                LinearLayoutManager.VERTICAL,
                false
        );
        layoutManager.scrollToPosition(0);
        rv.setLayoutManager(layoutManager);
    }


    public void launchEditActivity() {
        Intent intent = new Intent(getContext(), RecipeEditorActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}