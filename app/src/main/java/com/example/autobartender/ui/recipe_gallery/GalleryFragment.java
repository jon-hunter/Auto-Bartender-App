package com.example.autobartender.ui.recipe_gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.autobartender.databinding.FragmentRecipeGalleryBinding;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
private FragmentRecipeGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
        ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentRecipeGalleryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}