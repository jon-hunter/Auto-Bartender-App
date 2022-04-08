package com.example.autobartender.ui.recipe_editor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.autobartender.R;
import com.example.autobartender.utils.RecipeManager;
import com.example.autobartender.utils.RecipeManager.ReferenceIngredient;


public class RecipeEditorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_editor);

        Spinner spinner = findViewById(R.id.spinner_ingredient_choice);
        ArrayAdapter<ReferenceIngredient> adapter = new ArrayAdapter<ReferenceIngredient>(this, R.layout.spinner_item, RecipeManager.ingredientReference);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
    }
}