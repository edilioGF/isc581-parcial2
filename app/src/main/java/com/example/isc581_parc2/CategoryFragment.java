package com.example.isc581_parc2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CategoryFragment extends Fragment {

    private EditText mEditTextCategory;
    private Button mButtonSave;

    private DatabaseManager databaseManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        mEditTextCategory = view.findViewById(R.id.input_category);
        mButtonSave = view.findViewById(R.id.btn_create_category);

        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = mEditTextCategory.getText().toString();

                if (category.trim().length() <= 0) {
                    Toast.makeText(CategoryFragment.this.getContext(),
                            "Favor introducir una categoria", Toast.LENGTH_SHORT).show();

                    return;
                }

                databaseManager = new DatabaseManager(CategoryFragment.this.getContext());
                databaseManager.createCategory(category);

                mEditTextCategory.setText("");

                Toast.makeText(CategoryFragment.this.getContext(),
                        "Categoria agregada con exito", Toast.LENGTH_SHORT).show();

                CategoryFragment.this.getFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment, new ProductFragment(), "MANAGE_PRODUCTS")
                        .commit();
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        databaseManager.close();
        super.onDestroy();
    }
}