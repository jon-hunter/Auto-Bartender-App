package com.example.autobartender.ui.recipe_list;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autobartender.R;
import com.example.autobartender.databinding.FragmentRecipeListBinding;


public class RecipeListFragment extends Fragment {

    private RecipeListViewModel galleryViewModel;
    private FragmentRecipeListBinding binding;

    private RecyclerView rv;
    private RecipeView_RVA rva;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.galleryViewModel = new ViewModelProvider(this).get(RecipeListViewModel.class);
        this.binding = FragmentRecipeListBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Init the recyclerview
         rv=getActivity().findViewById(R.id.rvRecipeList);
         rva = new RecipeView_RVA(view.getContext());
         rv.setAdapter(rva);

        GridLayoutManager layoutManager = new GridLayoutManager(
                view.getContext(),
                1,
                LinearLayoutManager.VERTICAL,
                false
        );
        layoutManager.scrollToPosition(0);
        rv.setLayoutManager(layoutManager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}