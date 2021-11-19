package com.example.autobartender.ui.recipe_list;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autobartender.MainDataSingleton;
import com.example.autobartender.R;
import com.example.autobartender.RecyclerView_Adapter;
import com.example.autobartender.databinding.FragmentRecipeListBinding;


public class RecipeListFragment extends Fragment {

    private static final String TAG = "RecipeListFragment";
    private RecipeListViewModel galleryViewModel;
    private FragmentRecipeListBinding binding;
    private Context context;

    private RecyclerView rv;
    private RecyclerView_Adapter rva;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.galleryViewModel = new ViewModelProvider(this).get(RecipeListViewModel.class);
        this.binding = FragmentRecipeListBinding.inflate(inflater, container, false);
        this.context = container.getContext();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Init the recyclerview
        rv = getActivity().findViewById(R.id.rvRecipeList);
        rva = new RecyclerView_Adapter(this.context);
        rv.setAdapter(rva);
        // Set its click listener
        AdapterView.OnItemClickListener messageClickedHandler = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Log.d(TAG, "onItemClick: position: " + position);
            }
        };

        rv.setOnItemClickListener(messageClickedHandler);
        //TODO very upset that recyclerview doesnt implement this.
        //TODO see https://stackoverflow.com/a/24933117 for possible answer
        //TODO if that doesnt work then very carefully convert recyclerview to listview

        GridLayoutManager layoutManager = new GridLayoutManager(
                this.context,
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