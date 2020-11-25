package com.example.isc581_parc2;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.List;

public class ProductFragment extends Fragment {

    private EditText editTxtName;
    private EditText editTxtPrice;

    private Button addBtn;
    private Button saveBtn;
    private Button updatedBtn;
    private Button removeBtn;

    private Spinner categorySpinner;
    private ArrayAdapter<String> mAdapter;
    private List<String> allCategories;

    private DatabaseManager dbManager;

    private long _id;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        getActivity().setTitle("Agregar Producto");
        editTxtName = (EditText) view.findViewById(R.id.input_name);
        editTxtPrice = (EditText) view.findViewById(R.id.input_price);
        addBtn = (Button) view.findViewById(R.id.btn_add);
        saveBtn = (Button) view.findViewById(R.id.btn_save);
        updatedBtn = (Button) view.findViewById(R.id.btn_update);
        removeBtn = (Button) view.findViewById(R.id.btn_remove);

        //load spinner
        categorySpinner = (Spinner) view.findViewById(R.id.spinner_category);
        loadSpinnerData();

        dbManager = new DatabaseManager(getContext());

        final Bundle bundle = this.getArguments();
        if (bundle != null) {
            getActivity().setTitle("Modificar Producto");

            editTxtName.setText(bundle.getString("name"));
            editTxtPrice.setText(bundle.getString("price"));
            _id = Long.parseLong(bundle.getString("id"));

            categorySpinner.post(new Runnable() {
                @Override
                public void run() {
                    categorySpinner.setSelection(mAdapter.getPosition((String) bundle.get("category")));
                }
            });

            // manage buttons visibility
            saveBtn.setVisibility(View.GONE);
            updatedBtn.setVisibility(View.VISIBLE);
            removeBtn.setVisibility(View.VISIBLE);
        }

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductFragment.this.getFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment, new CategoryFragment())
                        .addToBackStack("MANAGE_CATEGORIES").commit();

//            startActivity(new Intent(getContext(),ManageCategoriesActivity.class));
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductFragment.this.manageProduct(bundle);
            }
        });

        updatedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductFragment.this.manageProduct(bundle);
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ProductFragment.this.getContext()).setTitle("Borrar Producto")
                        .setMessage("El producto sera eliminado permanentemente, esta seguro de realizar esta acción?")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbManager.removeProduct(_id);
                                Toast.makeText(ProductFragment.this.getContext(), "Producto eliminado exitosamente", Toast.LENGTH_SHORT).show();

                                // go to previous fragment
                                ProductFragment.this.getFragmentManager().beginTransaction()
                                        .replace(R.id.main_fragment, new ProductFragment(), "LIST_PRODUCTS")
                                        .addToBackStack("LIST_PRODUCTS").commit();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        });
        return view;
    }

    private void manageProduct(Bundle bundle) {
        String name = editTxtName.getText().toString();
        String price = editTxtPrice.getText().toString();
        String category = allCategories.size() > 0 ? categorySpinner.getSelectedItem().toString() : "";
        int count = categorySpinner.getAdapter() != null ? categorySpinner.getAdapter().getCount() : 0;

        if (name.trim().length() <= 0 || price.trim().length() <= 0 || count == 0) {
            Toast.makeText(getContext(), "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String ACTION = "creado";
        if (bundle != null) {
            dbManager.updateProducts(_id, name, price, category);
            ACTION = "actualizado";
        } else {
            dbManager.createProduct(name, price, category);
        }

        Toast.makeText(getContext(), "Producto " + ACTION + " exitosamente", Toast.LENGTH_SHORT).show();
        getFragmentManager().beginTransaction()
                .replace(R.id.main_fragment, new ProductFragment(), "LIST_PRODUCTS")
                .addToBackStack("LIST_PRODUCTS").commit();
    }

    public void loadSpinnerData() {
        DatabaseManager dbManager = new DatabaseManager( getContext() );
        allCategories = dbManager.getCategories();
        mAdapter = new ArrayAdapter<>( getContext(), android.R.layout.simple_spinner_item, allCategories );
        mAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        categorySpinner.setAdapter(mAdapter);
    }
}